(ns owlet.rf-util
  "Useful extensions to the re-frame framework, usable in any re-frame app."
  (:require [re-frame.core :as rf]))


(defn- array-map-assoc
  "Just like assoc, except that if old-map is nil, an array map is returned,
  not a hash map. Then, as the map grows from subsequent uses of assoc, update,
  or array-map-assoc, insert order is maintained."
  [old-map & args]
  (apply assoc (or old-map (array-map)) args))


(defn- pipeline-composer
  "Composes the functions in the pipeline at pipeline-path in @xfrms-atom,
  returning a function as follows: It takes a starting-value with possibly
  more-args, and returns a value suitable to be assigned to or generated from a
  value at a location in app-db. Each transform function in the pipeline must
  similarly take some value and more-args -- yes, the very same more-args. The
  first will receive starting-value and more-args, the second will receive the
  value returned by the first and more-args, and so on.

  Importantly, xfrms-atom is dereferenced each time the returned function is
  called, not when the returned function is generated. This allows namespaces
  loaded at any time in the future to add behavior to a setter or getter,
  independently of when the reg-setter or reg-getter runs."
  [xfrms-atom pipeline-path]
  (fn [starting-value & more-args]
    (let [accum    (fn [result-so-far f]
                     (apply f result-so-far more-args))
          xfrm-fns (vals (get-in @xfrms-atom pipeline-path))]
      (reduce accum starting-value xfrm-fns))))


(defn pipe-transform!
  "Puts the transform function f into the map tree in atom xfrms-atom. The
  function will be associated with the given key in an array map, which will
  be at path pipeline-path. The position of [key f] in the array map will
  follow the order in which pipe-transform! was repeatedly called, hence
  constructing a _pipeline_ of transform functions at pipeline-path.

  That is, if you call this function later with a new key and f and the same
  other arguments, then the new f will appear later in the pipeline
  (vals (get-in @xfrms-atom [set-or-get-key evt-or-qry-key])).

  You can replace an existing transform function by calling again with the
  same key and other arguments. Its position in the pipeline will be unchanged.
  If f is nil (or any non-function), then the key and any associated function
  will be removed from the pipeline."
  [xfrms-atom pipeline-path key f]
  (swap! xfrms-atom
         update-in pipeline-path
                   (fn [pipeline]
                     (if (ifn? f)
                       (array-map-assoc pipeline key f)
                       (dissoc pipeline key)))))


(def transforms
  "Normally, you will not need to use this atom directly. Call functions
  pipe-setter-transform! or pipe-getter-transform! instead.

  This is an atom containing a map of maps of maps, where paths like [::setter
  :my-evt-id :my-xform] or [::getter :my-qry-id :my-xform] yield a setter or
  getter transform function. In the case of ::setter, the function transforms
  its first argument (along with all but the first two elements of the event
  vector) to a value to be stored in app-db. In the case of ::getter, the
  function transforms its argument (along with all but the first element of
  the query vector) to a value for display in a view. The first argument to a
  ::setter transform function is the second element of the event vector OR the
  result of the previous transform function in a pipeline of such functions.
  The first argument to a ::getter transform function is the value from app-db
  OR the result of the previous transform function in a pipeline of such
  functions."
  (atom (hash-map
          ::setter (hash-map)
          ::getter (hash-map))))


;;;;
;;;; The following are normally the only functions you need to use.
;;;;


(defn pipe-setter-transform!
  "Adds the transform function f to the ::setter pipeline associated with the
  given event or query key in the map tree in atom transforms. See function
  pipe-transform!"
  [evt-or-qry-key key f]
  (pipe-transform! transforms [::setter evt-or-qry-key] key f))


(defn pipe-getter-transform!
  "Adds the transform function f to the ::getter pipeline associated with the
  given event or query key in the map tree in atom transforms. See function
  pipe-transform!"
  [evt-or-qry-key key f]
  (pipe-transform! transforms [::getter evt-or-qry-key] key f))


(defn reg-setter
  "Provides an easy way to register a new handler returning a map that differs
  from the given db map only at the location at the given path vector. Simply
  provide the event-id keyword and the db-path vector, and any new value from
  an event will be assoc-in to the map in atom re-frame.db/app-db at that
  location.

  Optionally, the stored value can be the result of a function you provide that
  takes as arguments the new value and any remaining arguments from the event
  vector. For example, if we called

    (reg-setter :my-handler
                [:path :in :app-db]
                (fn [new-val up?]
                  (if up? (upper-case new-val) new-val)))

    (dispatch [:my-handler \"new words\" true])
    ; Note [event-id new-value remaining-arg] event vector.

  Now, evaluating

    (get-in @app-db [:path :in :app-db])

  will result in \"NEW WORDS\".

  If no function argument is given, then whenever this registered event handler
  runs, the pipeline of transform functions associated with event-id is looked
  up. (The transform functions were already saved by your calls, if any, to
  pipe-setter-transform!.) If the pipeline is found, then the first function in
  it is called with the second element of the event vector, the next function
  is called with that result, and so on. Additional arguments on each of these
  calls will be the elements following the first two in the event vector (which
  are just event-id and the value to start the pipeline). The final result is
  saved in app-db at db-path."

  ([event-id db-path]
   (reg-setter event-id
               db-path
               (pipeline-composer transforms [::setter event-id])))

  ([event-id db-path f]
   (rf/reg-event-db
     event-id
     (fn [db [_ & event-vector-vals]]
       ; If event-vector-vals is non-nil, its first element would ordinarily be
       ; the value assigned in db at db-path. Here, we first apply the user-
       ; supplied function f to the value plus the rest of event-vector-vals.
       (assoc-in db db-path (apply f event-vector-vals))))))


(defn reg-getter
  "Provides an easy way to register a new subscription that just retrieves the
  value in the given db map at the given path. Simply provide the query-id
  keyword designating the new subscription and the path vector map to the data
  of interest in atom re-frame.db/app-db. Optionally, the value returned by the
  subscription's reaction may be the result of a function you provide. It
  should take as arguments the new value and additional arguments in the
  subscription vector following the query key. For example, if we called

    (reg-getter :my-sub
                [:path :in :app-db]
                (fn [new-val up?]
                  (if up? (upper-case new-val) new-val)))

    (defn some-component []
      (let [the-word (subscribe [:my-sub true])]
        ; Note [query-id additional-arg] vector.
        (fn ...

  Now, when @app-db changes, say after executing something like

    (swap! app-db assoc-in [:path :in :app-db] \"new words\")

  some-component will render using \"NEW WORDS\" as the value from @the-word.

  If no function argument is given, then whenever this registered subscription
  runs, the pipeline of transform functions associated with query-id is looked
  up in the map tree in atom transforms. (The transform functions were already
  saved by your calls, if any, to pipe-getter-transform!.) If the pipeline is
  found, then the first function in it is called with the value from app-db at
  db-path, the next function is called with that result, and so on. Additional
  arguments on each of these calls will be the elements of the query vector
  triggering this reaction, omitting the first element of the vector (which is
  always query-id). The final result is returned by the subscription for use by
  the subscribing component."

  ([query-id db-path]
   (reg-getter query-id
               db-path
               (pipeline-composer transforms [::getter query-id])))

  ([query-id db-path f]
   (rf/reg-sub
     query-id
     (fn [db [_ & args]]
       (apply f (get-in db db-path) args)))))

