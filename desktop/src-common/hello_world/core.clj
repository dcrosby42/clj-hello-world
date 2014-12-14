(ns hello-world.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defn- mdec [x] (- x 5))
(defn- minc [x] (+ x 5))

(defn move
  [entity direction]
  (case direction
    :down (assoc entity :y (mdec (:y entity)))
    :up (assoc entity :y (minc (:y entity)))
    :right (assoc entity :x (minc (:x entity)))
    :left (assoc entity :x (mdec (:x entity)))
    nil))

(defn rotate
  [entity direction]
  (case direction
    :right (assoc entity :angle (mdec (:angle entity)))
    :left (assoc entity :angle (minc (:angle entity)))
    ;; :left (assoc entity :y (minc (:y entity)))
    nil))

(declare reset)

(def keymap
  {(key-code :escape)     (fn [_ _] (reset))
   (key-code :dpad-up)    (fn [_ entities] (move (first entities) :up))
   (key-code :dpad-down)  (fn [_ entities] (move (first entities) :down))
   (key-code :dpad-left)  (fn [_ entities] (move (first entities) :left))
   (key-code :dpad-right) (fn [_ entities] (move (first entities) :right))
   (key-code :a)          (fn [_ entities] (rotate (first entities) :left))
   (key-code :d)          (fn [_ entities] (rotate (first entities) :right))
   })

(defn new-icon []
  (-> (texture "clojure-icon.gif")
      (assoc 
        :x 200 :y 200
        :width 100 :height 100
        :angle 45 
        :origin-x 50 :origin-y 50)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    ;(label "Hello world!" (color :white))
    (add-timer! screen :spawn-enemy 0.1 2 5)
    (add-timer! screen :spawn-friend 1 4 3)
    (new-icon)
    )

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :spawn-enemy (println "SPAWN ENEMY!")
      :spawn-friend (println "Spawn friend")
      nil))

  :on-resize
  (fn [screen entities]
    (height! screen 600))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))

  :on-key-down
  (fn [screen entities]
    (if-let [f (get keymap (:key screen))]
      (f screen entities))
    )

  :on-touch-down
  (fn [screen entities]
    (cond
      (> (game :point-y) (* (game :height) (/ 2 3)))
      (move (first entities) :up)
      (< (game :point-y) (/ (game :height) 3))
      (move (first entities) :down)
      (> (game :point-x) (* (game :width) (/ 2 3)))
      (move (first entities) :right)
      (< (game :point-x) (/ (game :width) 3))
      (move (first entities) :left)))
  )

(defgame hello-world-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

(defn reset []
  (println "Reset!")
  (on-gl (set-screen! hello-world-game main-screen)))
