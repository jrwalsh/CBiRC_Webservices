<?php
$org = isset($_GET["org"]) ? strtoupper($_GET["org"]) : false;
$frame = isset($_GET["frame"]) ? strtoupper($_GET["frame"]) : false;
$slot = isset($_GET["slot"]) ? strtoupper($_GET["slot"]) : false;

if(!$org or !$frame or strstr($org,";") or strstr($frame,";")) {
        echo "Incorrect usage. Please supply org=, frame=, and slot=";
}
else {
        $filename = "$org:$frame.txt";
        $cmd = ("java -jar GetFrame.jar tht.vrac.iastate.edu 4444 $org $frame $slot");
        exec($cmd, $output, $ret);
        foreach ($output as $value) {
                echo $value;
                echo "<br />";
        }
}
?>