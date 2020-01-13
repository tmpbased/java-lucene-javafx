;;;; my-prj.asd

(asdf:defsystem #:my-prj
  :description "Describe my-prj here"
  :author "Your Name <your.name@example.com>"
  :license  "Specify license here"
  :depends-on (#:sdl2 #:cl-opengl #:cl-cairo2)
  :version "0.0.1"
  :serial t
  :components ((:file "package")
               (:file "my-prj")))
