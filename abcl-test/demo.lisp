(defun lispfunction ()
  (format t "in lispfunction~a~%" (ql:system-apropos "regex")))
  
(defun void-function (param)
  (let* ((class (jclass "abcl.jfx.App"))
   (intclass (jclass "int"))
   (method (jmethod class "addTwoNumbers" intclass intclass))
   (result (jcall method param 2 4)))
    (format t "in void-function, result of calling addTwoNumbers(2, 4): ~a~%" result)))