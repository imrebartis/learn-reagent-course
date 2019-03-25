(ns giggin.fb.db
  (:require ["firebase/app" :refer [database]]
            [clojure.string :as str]
            [giggin.state :as state]))

(defn db-ref
  [path] ;; ["users" userId "name"] -> "users/userId/name"
  (.ref (database) (str/join "/" path))) ;; in JS: database().ref('users/' + userId (from https://firebase.google.com/docs/database/web/read-and-write?authuser=0)

(defn db-save!
  [path value]
  (.set (db-ref path) value))

  ; from https://firebase.google.com/docs/database/web/read-and-write?authuser=0#listen_for_value_events
  ; var starCountRef = firebase.database().ref('posts/' + postId + '/starCount');
  ; starCountRef.on('value', function(snapshot) {
  ;   updateStarCount(postElement, snapshot.val());
  ; });

  (defn db-subscribe
    [path]
    (.on (db-ref path)
          "value"
          (fn [snapshot]
            (reset! state/gigs (js->clj (.val snapshot) :keywordize-keys true)))))
