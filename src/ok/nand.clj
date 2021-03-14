(ns ok.nand)

(def ex-state-v0 {:charge-map {:a 1 :b 1 :c 0}
                  :nand-gates [{:ins [:a :b]
                                :out :c}]})

; update state v0
; ---------------

(def empty-state {:charge-map {} :nand-gates []})

(defn charge [state wire]
  (get-in state [:charge-map wire]))

(defn charges [state wires]
  (map (partial charge state) wires))

(defn set-charge [state wire charge]
  (assoc-in state [:charge-map wire] charge))

(defn wire-nand-gate [state a b o]
  (update state :nand-gates conj {:ins [a b] :out o}))

