(ns ui.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [clojure.string :as string :refer [split-lines]]))

(defonce proc (js/require "child_process"))

(enable-console-print!)

(defonce app-state {:counter/count 0
                    :shell/result ""
                    :shell/command ""})

(defmulti read om/dispatch)

(defmulti mutate om/dispatch)

(def reconciler
  (om/reconciler {:state app-state
                  :parser (om/parser {:read read
                                      :mutate mutate})}))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

(defmethod mutate 'counter/increment
  [{:keys [state] :as env} _ _]
  {:value {:keys [:counter/count]}
   :action #(swap! state update :counter/count inc)})

(defmethod mutate 'shell/set-command
  [{:keys [state] :as env} _ {:keys [command]}]
  {:value {:keys [:shell/command]}
   :action #(swap! state assoc :shell/command command)})

(defmethod mutate 'shell/append-result
  [{:keys [state] :as env} _ {:keys [output]}]
  {:value {:keys [:shell/result]}
   :action #(swap! state update :shell/result str output)})

(defmethod mutate 'shell/clear-result
  [{:keys [state] :as env} _ _]
  {:value {:keys [:shell/result]}
   :action #(swap! state assoc :shell/result "")})

(defn append-to-out [out]
  (om/transact! reconciler [`(shell/append-result {:output ~out})
                            :shell/result]))

(defn run-process [command]
  (when-not (empty? command)
    (println "Running command" command)
    (let [[cmd & args] (string/split command #"\s")
          js-args (clj->js (or args []))
          p (.spawn proc cmd js-args)]
      (.on p "error" (comp append-to-out
                           #(str % "\n")))
      (.on (.-stderr p) "data" append-to-out)
      (.on (.-stdout p) "data" append-to-out))
    (om/transact! reconciler [`(shell/clear-result) :shell/result])))

(def join-lines (partial string/join "\n"))

(defui Root
  static om/IQuery
  (query [_]
    [:counter/count :shell/result :shell/command])

  Object
  (render [this]
    (let [{:keys [counter/count shell/command shell/result]} (om/props this)]
      (html
        [:div
         [:div.logos
          [:img.electron {:src "img/electron-logo.png"}]
          [:img.cljs {:src "img/cljs-logo.svg"}]
          [:img.reagent {:src "img/reagent-logo.png"}]]
         [:pre "Versions:"
          [:p (str "Node     " js/process.version)]
          [:p (str "Electron " ((js->clj js/process.versions) "electron"))]
          [:p (str "Chromium " ((js->clj js/process.versions) "chrome"))]]
         [:button
          {:on-click #(om/transact! this ['(counter/increment)
                                          :counter/count])}
          (str "Clicked " count " times")]
         [:div
          [:form
           {:on-submit
            (fn [e]
              (.preventDefault e)
              (run-process command))}
           [:input#command
            {:type :text
             :on-change
             (fn [e]
               (om/transact!
                 this
                 [`(shell/set-command {:command ~(-> e .-target .-value)})
                  :shell/command]))
             :value command
             :placeholder "type in shell command"}]]]
         [:pre (join-lines (take 100 (reverse (split-lines result))))]]))))

(om/add-root! reconciler Root (gdom/getElement "app-container"))
