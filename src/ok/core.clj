(ns ok.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def global-width 600)
(def global-height 600)
(def number-of-particles 100)
(def particle-size 20)

(defn particle []
  {:x (q/random global-width)
   :y (q/random global-height)
   :vx (q/random -5 5)
   :vy (q/random -5 5)
   :color (q/random 255)})

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  {:particles (take number-of-particles (repeatedly particle))})

(defn move-particle [particle]
  (let [new-x (+ (:x particle) (:vx particle))
        new-y (+ (:y particle) (:vy particle))
        x-changes (if (or (< new-x 0) (> new-x global-width))
                    {:vx (- (:vx particle))}
                    {:x (+ (:x particle) (:vx particle))})
        y-changes (if (or (< new-y 0) (> new-y global-height))
                    {:vy (- (:vy particle))}
                    {:y (+ (:y particle) (:vy particle))})]
    (merge particle x-changes y-changes)))

(defn update-state [state]
  (assoc state :particles (map move-particle (:particles state))))

(defn draw-state [state]
  (q/background 240)
  (doseq [p (:particles state)]
    (q/fill (:color p) 255 255)
    (let [x (:x p)
          y (:y p)]
      (q/ellipse x y particle-size particle-size))))

(q/defsketch ok
  :title "Vectors"
  :size [global-width global-height]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
