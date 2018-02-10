(ns owlet.routes.contentful
  (:require [compojure.core :refer [defroutes GET POST PUT]]
            [selmer.parser :refer [render-file]]
            [selmer.filters :refer [add-filter!]]
            [ring.util.http-response :refer [ok not-found internal-server-error]]
            [ring.util.response :refer [redirect]]
            [compojure.api.sweet :refer [context]]
            [org.httpkit.client :as http]
            [mailgun.mail :as mail]
            [cheshire.core :as json]
            [camel-snake-kebab.core :refer [->kebab-case]]
            [owlet.constants :as constants])
  (:use [net.cgrand.enlive-html :as html]))

(def creds {:key    (System/getenv "MMM_MAILGUN_API_KEY")
            :domain "mg.codefordenver.org"})

(defonce OWLET-ACTIVITIES-3-MANAGEMENT-AUTH-TOKEN
         (System/getenv "OWLET_ACTIVITIES_3_MANAGEMENT_AUTH_TOKEN"))

(defonce OWLET-ACTIVITIES-3-DELIVERY-AUTH-TOKEN
         (System/getenv "OWLET_ACTIVITIES_3_DELIVERY_AUTH_TOKEN"))

(def owlet-url "http://owlet.codefordenver.org")

(defn epoch [] (int (/ (System/currentTimeMillis) 1000)))

(defonce subscribers-endpoint "https://owlet-users.firebaseio.com/subscribers.json")

(defn subscriber-endpoint [id]
  (str "https://owlet-users.firebaseio.com/subscribers/" id ".json"))

(add-filter! :kebab #(->kebab-case %))

(defn- get-space-metadata
  "GET all content types in owlet-activities-3 space"
  [space-id headers]
  (http/get (format "https://api.contentful.com/spaces/%1s/content_types" space-id) headers))

(defn- get-entry-by-id [space-id entry-id]
  (http/get (format "https://cdn.contentful.com/spaces/%1s/entries/%2s" space-id entry-id)
            {:headers {"Authorization" (str "Bearer " OWLET-ACTIVITIES-3-DELIVERY-AUTH-TOKEN)}}))

(defn- get-asset-by-id [space-id asset-id]
  (http/get (format "https://cdn.contentful.com/spaces/%1s/assets/%2s" space-id asset-id)
            {:headers {"Authorization" (str "Bearer " OWLET-ACTIVITIES-3-DELIVERY-AUTH-TOKEN)}}))

(defn- filter-entries [content-type items]
  (filter #(= content-type
              (-> % (get-in [:sys :contentType :sys :id])))
          items))

(defn- image-by-id
  "Maps image IDs to associated URL, width, and height."
  [assets]
  (->> assets
       (map
         (juxt
           (comp :id :sys)
           #(hash-map
              :url (get-in % [:fields :file :url])
              :w (get-in % [:fields :file :details :image :width])
              :h (get-in % [:fields :file :details :image :height]))))
       (into {})))

(defn- keywordize-name [name]
  (-> name ->kebab-case keyword))

(def remove-nil (partial remove nil?))

(defn- process-activity [activity branches tags platforms assets]
  (when (= (get-in activity [:sys :contentType :sys :id]) "klipseActivity")
    (html/deftemplate slideshow-template
      (java.io.StringReader. (slurp (str (get-in activity [:fields :embedUrl]) "/embed?style=light&postMessageEvents=true"))) []
      [:html :body [:script (attr-contains :src "//assets.slid.es/assets/deck")]] (do-> (html/set-attr :src "../js/vendor/reveal.js") (html/remove-attr :defer))
      [:html :body [:script (attr-contains :src "//assets.slid.es/assets/application")]] (html/remove-attr :defer)
      [:html :body] (append (html [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/headjs/1.0.3/head.js"}]))
      [:html :body] (append (html [:script (str "SL.util.setupReveal();")]))))
  (-> activity
      (assoc-in [:fields :iframeContent] (apply str (slideshow-template)))
      ; Adds :branches data using :branchRefs
      (assoc-in [:fields :branches]
                (map
                  (fn [branchRef]
                    (apply #(:fields %) (filter #(= (-> branchRef :sys :id) (-> % :sys :id)) branches)))
                  (-> activity :fields :branchRefs)))
      ;; Adds :tags data using :tagRefs
      (assoc-in [:fields :tags]
                (map
                  (fn [tag-ref]
                    (apply #(:fields %) (filter #(= (-> tag-ref :sys :id) (-> % :sys :id)) tags)))
                  (-> activity :fields :tagRefs)))
      ; Adds :platform data using :platformRef
      (assoc-in [:fields :platform]
                (some #(when (= (get-in activity [:fields :platformRef :sys :id])
                                (get-in % [:sys :id]))
                         (hash-map :name (get-in % [:fields :name])
                                   :search-name (str (->kebab-case (get-in % [:fields :name])))
                                   :color (get-in % [:fields :color])
                                   :requiresDownload (get-in % [:fields :requiresDownload])
                                   :free (get-in % [:fields :free])))
                      platforms))
      ; Adds preview img. URL at [.. :sys :url]
      (update-in [:fields :preview :sys]
                 (fn [{id :id :as sys}]
                   (assoc sys
                     :url
                     (get-in (image-by-id assets) [id :url]))))
      ; Adds :image-gallery-items
      (assoc-in [:fields :image-gallery-items]
                (->> (get-in activity [:fields :imageGallery])
                     (map (comp :id :sys))                            ; Gallery image ids.
                     (mapv (image-by-id assets))))
      ; Add :tag-
      (assoc-in [:tag-set] (or (some->> activity
                                        :fields
                                        :tags
                                        remove-nil
                                        seq                 ; some->> gives nil if empty
                                        (map keywordize-name)
                                        set)
                               activity))
      ;; Removes refs since no longer needed in the front-end
      (update :fields #(apply dissoc % [:branchRefs :tagRefs :platformRef]))))

(defn- process-activities
  [activities branches tags platforms assets]
  (for [activity activities]
    (process-activity activity branches tags platforms assets)))


(defn handle-get-all-entries-for-given-space

  "asynchronously GET all entries for given space
	optionally pass library-view=true param to get all entries for given space"

  [req]


  (let [{:keys [space-id]} (:params req)
        opts1 {:headers {"Authorization" (str "Bearer " OWLET-ACTIVITIES-3-MANAGEMENT-AUTH-TOKEN)}}
        opts2 {:headers {"Authorization" (str "Bearer " OWLET-ACTIVITIES-3-DELIVERY-AUTH-TOKEN)}}]
    (let [{:keys [status body]}
          @(http/get (format "https://cdn.contentful.com/spaces/%1s/entries?" space-id) opts2)
          metadata (get-space-metadata space-id opts1)]
      (if (= status 200)
        (let [entries (json/parse-string body true)
              assets (get-in entries [:includes :Asset])
              branches (filter-entries "branch" (:items entries))
              platforms (filter-entries "platform" (:items entries))
              tags (filter-entries "tag" (:items entries))
              activities (concat (filter-entries "klipseActivity" (:items entries))
                                 (filter-entries "activity" (:items entries)))]

          (ok {:activities (process-activities activities branches tags platforms assets)
               :branches   branches
               :platforms  platforms
               :tags       tags}))
        (not-found status)))))

(defn- compose-new-activity-email
  "Pluck relevant keys from activity payload and return { subject, body }"
  [activity]
  (let [id (-> activity :sys :id)
        title (-> activity :fields :title :en-US)
        author (-> activity :fields :author :en-US)
        image-url (-> activity :fields :preview :sys :url)
        platform-color (-> activity :fields :platform :color)
        platform-name (-> activity :fields :platform :name)
        tags (-> activity :fields :tags :en-US)
        description (-> activity :fields :summary :en-US)
        subject (format "New Owlet Activity Published: %s by %s" title author)
        url (format "http://owlet.codefordenver.org/#/activity/#!%s" id)
        html (render-file "activity-email.html" {:activity-id          id
                                                 :activity-image-url   image-url
                                                 :activity-title       title
                                                 :platform-color       platform-color
                                                 :platform-name        platform-name
                                                 :activity-description description
                                                 :tag-names          tags})]
    (hash-map :subject subject
              :html html)))


(defn handle-confirmation [req]
  (let [id (get-in req [:params :id])
        {:keys [status body]} @(http/get (subscriber-endpoint id))]
    (if (= 200 status)
      (let [subscriber (json/parse-string body true)
            confirmed? (:confirmed subscriber)
            {:keys [status body]}
            @(http/put (subscriber-endpoint id)
                       {:body (json/encode
                                {:email     (:email subscriber)
                                 :confirmed (when-not (nil? confirmed?)
                                              (not confirmed?))})})]
        (if (= 200 status)
          (redirect (if confirmed?
                      (str owlet-url "/#/unsubscribed/" (:email subscriber))
                      (str owlet-url "/#/subscribed/" (:email subscriber))))
          (internal-server-error status)))
      (internal-server-error status))))


(defn- send-confirmation-email [email id subscribing]
  "Sends confirmation email"
  (let [url (format "http://owlet.codefordenver.org/#/confirm/%1s" id)
        html (if (= subscribing true)
               (render-file "confirm-email.html" {:url url :un ""})
               (render-file "confirm-email.html" {:url url :un "un"}))
        mail-transact!
        (mail/send-mail creds
                        {:from    "owlet@mmmanyfold.com"
                         :to      email
                         :subject "Please confirm your email address"
                         :html    html})]
    (when (= (:status mail-transact!) 200)
      (prn (str "Sent confirmation email to " email)))))

(defn is-new-activity?
  "return whether or not this activity is being published for the first time"
  [activity]
  (let [revision (get-in activity [:sys :revision])
        activity-type (get-in activity [:sys :contentType :sys :id])]
    (and (or (= "activity" activity-type)
             (= "klipseActivity" activity-type))
         ;; checking that this is the first published version of this activity content
         (= 1 revision))))


(defn make-email-recipients
  "Returns a list of subscribers delimited by a comma"
  [users]
  (let [values (map val users)
        emails (for [user values
                     :when (:confirmed user)]
                 (:email user))]
    (clojure.string/join "," emails)))

(defn get-subscribers-from-firebase
  "Returns a list of subscribers from firebase endpoint"
  [endpoint]
  (let [{:keys [status body]} @(http/get endpoint)]
    (if (= 200 status)
      (let [users->json (json/parse-string body true)]
        (make-email-recipients users->json))
      (internal-server-error "Unable to retrieve subscribers from firebase endpoint"))))

(defn get-activity-image-thumbnail
  "Fetch, process & return activity with activity image thumbnail, provides default if none is found"
  [activity]
  (let [space-id (get-in activity [:sys :space :sys :id])
        default-thumbnail-url "owlet.codefordenver.org/img/default-thumbnail.png"]
    (if-let [asset-id (get-in activity [:fields :preview :en-US :sys :id])]
      (let [{:keys [status body]} @(get-asset-by-id space-id asset-id)]
        (if (= 200 status)
          (let [body (json/parse-string body true)
                asset-url (get-in body [:fields :file :url])]
               (-> activity
                   (assoc-in [:fields :preview :sys :url] asset-url)))
          (internal-server-error (str "Unable to retrieve activity thumbnail with asset-id:" asset-id))))
      ;; return with default thumbnail
      (-> activity
          (assoc-in [:fields :preview :sys :url] default-thumbnail-url)))))

(defn get-platform-metadata-for-activity
  "Fetch, process & return activity metadata details"
  [activity]
  (let [space-id (get-in activity [:sys :space :sys :id])
        entry-id (get-in activity [:fields :platformRef :en-US :sys :id])]
    (let [{:keys [status body]} @(get-entry-by-id space-id entry-id)]
      (if (= 200 status)
        (let [body (json/parse-string body true)
              platform-name (get-in body [:fields :name])
              platform-color (get-in body [:fields :color])]
          (-> activity
              (assoc-in [:fields :platform :name] platform-name)
              (assoc-in [:fields :platform :color] platform-color)))
        (internal-server-error "Unable to retrieve activity's entry metadata")))))

(defn handle-activity-publish
  "Sends email to list of subscribers"
  [req]
  (let [activity-payload (:params req)]
    (if (is-new-activity? activity-payload)
      (let [subscribers (get-subscribers-from-firebase subscribers-endpoint)
            activity (-> activity-payload
                         (get-activity-image-thumbnail)
                         (get-platform-metadata-for-activity))]
        (prn activity)
        (let [{:keys [subject html]} (compose-new-activity-email activity)
              mail-transaction (mail/send-mail creds
                                               {:from    "owlet@mmmanyfold.com"
                                                :to      "owlet@mmmanyfold.com"
                                                :bcc     subscribers
                                                :subject subject
                                                :html    html})]
          (if (= (:status mail-transaction) 200)
            (ok "Emailed Subscribers Successfully.")
            (internal-server-error mail-transaction))))
      (ok "Not a new activity."))))

(defn handle-activity-subscribe

  "handles new subscription request
	 -checks list of subs b4 adding to list; ie no duplicates"

  [req]

  (let [email (-> req :params :email)
        {:keys [status body]} @(http/get subscribers-endpoint)]
    (let [coll (json/parse-string body true)]
      (if (= status 200)
        (if-let [existing-user (some #(when (= (:email %) email) %) (vals coll))]
          (if (:confirmed existing-user)
            (ok constants/subscribed)
            (let [id (-> (filter (comp #{{:email email :confirmed false}} coll)
                                 (keys coll))
                         first
                         name)]
              (send-confirmation-email email id true)
              (ok constants/confirmation-resent)))
          (let [id (epoch)
                {:keys [status body]}
                @(http/put (subscriber-endpoint id)
                           {:body (json/encode {:email     email
                                                :confirmed false})})]
            (if (= status 200)
              (do
                (send-confirmation-email email id true)
                (ok constants/confirmation-sent))
              (internal-server-error status))))
        (internal-server-error status)))))

(defn handle-activity-unsubscribe
  "handles unsubscribe request"
  [req]
  (let [email (-> req :params :email)
        {:keys [status body]} @(http/get subscribers-endpoint)]
    (let [coll (json/parse-string body true)]
      (if (= status 200)
        (if-let [existing-user (some #(when (= (:email %) email) %) (vals coll))]
          (if (:confirmed existing-user)
            (let [id (-> (filter (comp #{{:email email :confirmed true}} coll)
                                 (keys coll))
                         first
                         name)]
              (send-confirmation-email email id false)
              (ok constants/confirmation-sent))
            (ok constants/not-subscribed)))
        (internal-server-error)))))

(defroutes routes
           (context "/webhook" []
             (context "/content" []
               (POST "/email" {params :params} handle-activity-publish)
               (PUT "/unsubscribe" {params :params} handle-activity-unsubscribe)
               (PUT "/subscribe" {params :params} handle-activity-subscribe)
               (GET "/confirm" {params :params} handle-confirmation)))
           (context "/content" []
             (GET "/space" {params :params} handle-get-all-entries-for-given-space)))
