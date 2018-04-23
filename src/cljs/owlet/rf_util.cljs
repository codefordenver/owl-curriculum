(ns owlet.rf-util
  "Useful extensions to the re-frame framework, usable in any re-frame app."
  (:require [re-frame.core :as rf]))


(defn- array-map-assoc
  "Just like assoc, except that if old-map is nil, an array map is returned,
  not a hash map. Then, as the map grows from subsequent uses of assoc, update,
  or array-map-assoc, insert order is maintained.
  "
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
  loaded at any time in the future to add a pipelined function to a handler
  (a setter, a getter, or an event handler adorned with interceptor fx-pipeline)
  independently of when the handler runs.
  "
  [xfrms-atom pipeline-path]
  (fn [starting-value & more-args]
    (let [accum    (fn [result-so-far f]
                     (apply f result-so-far more-args))
          xfrm-fns (vals (get-in @xfrms-atom pipeline-path))]
      (reduce accum starting-value xfrm-fns))))


(defn pipe-transform!
  "Puts the transform function f into a pipeline in the map tree in atom
  xfrms-atom. Function f must take at least one argument, the value to be
  transformed through the pipeline. It must also handle additional values from
  the event or query triggering the handler that will run it, which is
  indicated by pipeline-path. Function f will be associated with keyword f-key
  in an array map at path pipeline-path. The position of [f-key f] in the array
  map will follow the order in which pipe-transform! was called, hence
  constructing a _pipeline_ of transform functions at pipeline-path.

  That is, if you call this function later with a new key and f and the same
  other arguments, then the new f will appear later in the pipeline of
  functions, which is just the sequence resulting from, say,

    (-> @xfrms-atom (get-in pipeline-path) vals)

  You can replace an existing transform function by calling again with the
  same f-key and same other arguments. Its position in the pipeline will be
  unchanged. If f is nil (or any non-function), then the key and any associated
  function will be removed from the pipeline.
  "
  [xfrms-atom pipeline-path f-key f]
  (swap! xfrms-atom
         update-in pipeline-path
                   (fn [pipeline]
                     (if (ifn? f)
                       (array-map-assoc pipeline f-key f)
                       (dissoc pipeline f-key)))))


(defn fx-pipeline-interceptor
  "Creates a re-frame interceptor that applies a pipeline of functions that
  transforms the :effects value in the context that flows through the
  interceptor. The pipeline is constructed by your calls to pipe-fx-transform!
  or pipe-transform! Then any event handler adorned with the interceptor will
  run the :effects through the pipeline, allowing you to trigger arbitrary
  effects.

  Note the differences from reg-setter: First, this function creates an
  interceptor, not a handler. It can be used as the second argument in the
  registration of any event handler, i.e., in reg-setter,
  re-frame.core/reg-event-db, re-frame.core/reg-event-fx,
  or re-frame.core/reg-event-ctx.

  Second, each function in your pipeline must transform its first argument,
  an effects map, to another effects map. The effects map is like one you
  would return from re-frame.core/reg-event-fx. This differs from setter
  transform functions, which must transform the VALUE to \"set\" in app-db.

  Third, every fx pipeline function must take exactly two arguments. The
  second argument is the :coeffects map, like the first argument to a handler
  registered by re-frame.core/reg-event-fx. This gives you access to the event
  as well as to app-db as it existed when the handler was dispatched to. Each
  function in a setter pipeline, on the other hand, takes the same number of
  arguments equal to the count of the event vector. (The value to assign, plus
  all but the first element from the event vector.)

  Thus, a handler adorned by an fx-pipeline-interceptor result is strictly
  more general than a reg-setter handler, since it can provide a new value
  for the :db key in the returned effects map, but a setter cannot trigger an
  arbitrary effect. It can depend upon more data as well, since it has access
  to the entire :coeffects map.

  Arguments: This function takes an arbitrary id you can use to identify the
  returned interceptor, and a transforms atom, as provided to pipe-transform!.
  "
  [arbitrary-interceptor-id xfrms-atom]
  (rf/->interceptor
    :id arbitrary-interceptor-id
    :after (fn [{{[event-id] :event :as coeffects} :coeffects, effects :effects
                 :as context}]
             (assoc context
               :effects
               ((pipeline-composer xfrms-atom [::fx event-id])
                effects
                coeffects)))))


(def transforms
  "Normally, you will not need to use this atom directly. Instead, it is
  modified by calling pipe-setter-transform!, pipe-getter-transform!, or
  pipe-fx-transform!.

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
  functions.
  "
  (atom (hash-map
          ::setter (hash-map)
          ::getter (hash-map)
          ::fx     (hash-map))))


(defn paths-to-xfrm-fns
  "Handy function to list the path vector to each transform function in the
  given transforms atom, or in owlet.rf-util/transforms if none is provided.
  This is useful for debugging.
  "
  ([] (paths-to-xfrm-fns transforms))
  ([xfrms-atom]
   (into [] (for [[type id-map]         @xfrms-atom
                  [evt-or-qry-id f-map] id-map
                  f-key                 (keys f-map)]
              [type evt-or-qry-id f-key]))))


(defn- piper!
  "Adds the given function to the end of the pipeline at path
  [type-key evt-or-qry-id]. The pipeline is actually a map that maintains
  the order of it's key/value pairs. Argument f-key is the key of the given
  function in that map.
  "
  [type-key evt-or-qry-id f-key f]
  (pipe-transform! transforms [type-key evt-or-qry-id] f-key f))


;;;;
;;;; The following are normally the only functions you need to use.
;;;;


(def pipe-setter-transform!
  "[event-key f-key f]

  Adds the transform function f to the ::setter pipeline associated with
  the given event key in the map tree in atom owlet.rf-util/transforms.
  The event key must be handled by the handler you register with reg-setter.
  See function pipe-transform!.
  "
  (partial piper! ::setter))


(def pipe-getter-transform!
  "[query-key f-key f]

  Adds the transform function f to the ::getter pipeline associated with
  the given query key in the map tree in atom owlet.rf-util/transforms.
  See function pipe-transform!
  "
  (partial piper! ::getter))


(def pipe-fx-transform!
  "[event-key f-key f]

  Adds the transform function f to the ::fx pipeline associated with the given
  event key in the map tree in atom owlet.rf-util/transforms. See function
  pipe-transform!
  "
  (partial piper! ::fx))


(defn reg-setter
  "Provides an easy way to register a new handler returning a map that differs
  from the given db map only at the location at the given path vector. Simply
  provide the event-id keyword and the db-path vector, and any new value from
  an event will be assoc-in to the map in atom re-frame.db/app-db at that
  location.

  Optionally, the stored value can be the result of a transform-fn you provide
  that takes as arguments the new value and any remaining arguments from the
  event vector. For example, if we called

      (reg-setter :my-handler
                  [:path :in :app-db]
                  ::transform-fn (fn [new-val up?]
                                   (if up? (upper-case new-val)
                                           new-val)))
      (dispatch [:my-handler \"new words\" true])
      ; Note [event-id new-value remaining-arg] event vector.

  Now, evaluating

      (get-in @app-db [:path :in :app-db])

  will result in \"NEW WORDS\".

  If no :transform-fn option is given, then whenever this registered event
  handler runs, the pipeline of transform functions associated with event-id is
  looked up. (The transform functions were already saved by your calls, if any,
  to pipe-setter-transform!.) If the pipeline is found, then the first function
  in it is called with the second element of the event vector, the next
  function is called with that result, and so on. Additional arguments on each
  of these calls will be the elements following the first two in the event
  vector (which are just event-id and the value to start the pipeline). The
  final result is saved in app-db at db-path.

  Additional optional arguments:

  (reg-setter ... :transforms-atom xfrms-atom)  Arg. xfrms-atom will be used
  in place of atom owlet.rf-util/transforms to obtain the pipeline of transform
  functions when option :transform-fn is absent. Ignored if the :transform-fn
  option is provided.

  (reg-setter ... :interceptors inter)  Arg. inter can be an interceptor or
  a sequence of interceptors, which will run as in re-frame.core/reg-event-db.
  "
  [event-id db-path & {:keys [transform-fn transforms-atom interceptors]}]
  (let [xfrms-atom (or transforms-atom transforms)
        f          (or transform-fn (pipeline-composer xfrms-atom
                                                       [::setter event-id]))]
    (rf/reg-event-db
      event-id
      interceptors
      (fn [db [_ & event-vector-vals]]
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
                  :transform-fn (fn [new-val up?]
                                  (if up? (upper-case new-val)
                                          new-val)))
      (defn some-component []
        (let [the-word (subscribe [:my-sub true])]
          ; Note [query-id additional-arg] vector.
          (fn ...

  Now, when @app-db changes, say after executing something like

      ... (db assoc-in [:path :in :app-db] \"new words\")

  in a handler, then some-component will render using \"NEW WORDS\" as the
  value from @the-word.

  If no :transform-fn option is given, then whenever this registered
  subscription runs, the pipeline of transform functions associated with
  query-id is looked up in the map tree in atom owlet.rf-util/transforms.
  (The transform functions were already saved by your calls, if any, to
  pipe-getter-transform!.) If the pipeline is found, then the first function
  in it is called with the value from app-db at db-path, the next function is
  called with that result, and so on. Additional arguments on each of these
  calls will be the elements of the query vector triggering this reaction,
  omitting the first element of the vector (which is always query-id). The
  final result is returned by the subscription for use by the subscribing
  component.

  Additional optional arguments:

  (reg-getter ... :transforms-atom xfrms-atom)  Arg. xfrms-atom will be used
  in place of atom owlet.rf-util/transforms to obtain the pipeline of transform
  functions when option :transform-fn is absent. Ignored if the :transform-fn
  option is provided.
  "
  [query-id db-path & {:keys [transform-fn transforms-atom]}]
  (let [xfrms-atom (or transforms-atom transforms)
        f          (or transform-fn (pipeline-composer xfrms-atom
                                      [::getter query-id]))]
    (rf/reg-sub
      query-id
      (fn [db [_ & args]]
        (apply f (get-in db db-path) args)))))


(def fx-pipeline
  "A re-frame interceptor that applies the associated pipeline of
  effects-transform functions in the tree in atom owlet.rf-util/transforms.
  The pipeline is found in @transforms at path [::fx the-event-id], where
  the-event-id is from the event being processed by the event handler adorned
  by fx-pipeline. Simply by providing fx-pipeline as the second argument (or in
  a sequence of interceptors as the second argument) to
  re-frame.core/reg-event-db, re-frame.core/reg-event-fx, or
  re-frame.core/reg-event-ctx, your handler will run the effects pipeline
  you've built using pipe-fx-transform!. You can also provide fx-pipeline to
  the :interceptors option in a call to reg-setter.
  "
  (fx-pipeline-interceptor ::fx-pipeline transforms))
