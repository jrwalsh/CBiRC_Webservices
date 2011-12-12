    (defun http-get (server port path)
    "Send a request for file `path' to an HTTP/0.9 server on host
    `server', port number `port'. Print the contents of the returned file
    to standard output."
    ;; Open connection
    (let ((socket (open-socket server port)))
    (unwind-protect
    (progn
    (format t "> Sending request to ~a:~a...~%" server port)
    ;; Send request
    (http-send-line socket (format nil "GET ~a HTTP/0.9~%~%" path))
    (force-output socket)
     
    ;; Read response and output it
    (format t "> Received response:~%")
    (loop
    (let ((line (read-line socket nil nil)))
    (unless line (return))
    (format t "~a~%" line))))
     
    ;; Close socket before exiting.
    (close socket))))
