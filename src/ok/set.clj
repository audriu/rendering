(ns ok.set)

(defn empty [_]false)

(defn add [e s]
  (fn [c] (if (= c e)
            true
            (s c))))

(defn rem [e s]
  (fn [c] (if (= c e)
            false
            (s c))))

