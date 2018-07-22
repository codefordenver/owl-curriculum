(ns owlet.firebase
  (:require [clojure.string :as str]))


(defn- threading-symbol
  [prefix sym]
  (-> prefix
    (str sym)
    (str/replace #"-+" "-")
    symbol))


(defmacro ^:private define-macros-based-on
  [& symbols]
  `(do
     ~@(for [a-> symbols]
         (let [fn-a-> (threading-symbol "fn-" a->)
               qual-fn-a-> (symbol (str (namespace ::here) "/" fn-a->))]
           `(do

              (defmacro ~fn-a->
                [& forms#]
                `(fn [~'x#] (~'~a-> ~'x# ~@forms#)))

              (defmacro ~(threading-symbol "then-" a->)
                [prom# & forms#]
                `(.then ~prom# (~'~qual-fn-a-> ~@forms#))))))))


(define-macros-based-on -> ->> as-> some-> some->> cond-> cond->>)
