(ns owlet.constants)

(def confirmation-sent #?(:clj 201
                          :cljs 201))

(def subscribed #?(:clj 409
                   :cljs 409))

(def not-subscribed #?(:clj 404
                       :cljs 404))

(def confirmation-resent #?(:clj 200
                             :cljs 200))
