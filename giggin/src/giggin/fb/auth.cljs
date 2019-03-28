(ns giggin.fb.auth
  (:require ["firebase/app" :as firebase]
            [giggin.fb.db :refer [db-save!]]
            [giggin.state :as state]))

  ;; from https://firebase.google.com/docs/auth/web/google-signin?authuser=0
  ;; var provider = new firebase.auth.GoogleAuthProvider();
  (defn sign-in-with-google
    []
    (let [provider (firebase/auth.GoogleAuthProvider.)] ;; the '.' in the end stands for 'new' in JS
    ;; firebase.auth().signInWithPopup(provider)
      (.signInWithPopup (firebase/auth) provider)))

  ;; firebase.auth().signOut()
  (defn sign-out
    []
    (.signOut (firebase/auth)))

  ;; from https://firebase.google.com/docs/auth/web/manage-users?authuser=0
  ;; firebase.auth().onAuthStateChanged(function(user)
  (defn on-auth-state-changed
  []
  (.onAuthStateChanged
   (firebase/auth)
   (fn
     [user]
     (if user
       (let [uid (.-uid user)
             display-name (.-displayName user)
             photo-url (.-photoURL user)
             email (.-email user)]
          (do
            (reset! state/user {:photo-url photo-url
                                :display-name display-name
                                :email email})
            (db-save!
             ["users" uid "profile"]
             #js {:photo-url photo-url
                   :display-name display-name
                   :email email})))
        (reset! state/user nil)))))
