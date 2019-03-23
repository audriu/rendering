(ns ok.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def global-width 600)
(def global-heigth 600)

(def init-state
  {:particles [
               {:color 0  :x 300 :y 100  :xspeed 2 :yspeed 5}
               {:color 50 :x 145  :y 50 }
               {:color 200 :x 200 :y 300 }
               ]})

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  init-state)

(defn update-state [state]
  state)

(defn draw-state [state]
  (q/background 240)
  (doseq [p (:particles state)]
    (println "ok")
    (q/fill (:color p) 255 255)
    (let [x (:x p)
          y (:y p)]
      (q/ellipse x y 100 100))))


(q/defsketch ok
  :title "Vectors"
  :size [global-width global-heigth]
                                        ; setup function called only once, during sketch initialization.
  :setup setup
                                        ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
                                        ; This sketch uses functional-mode middleware.
                                        ; Check quil wiki for more info about middlewares and particularly
                                        ; fun-mode.
  :middleware [m/fun-mode])
