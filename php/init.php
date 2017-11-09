<?php  

#echo php_ini_loaded_file();
#connect to the mysql server
$host   =       "address.of.server";
$user   =       "username";
$pwd    =       "password";
$db     =       "database.being.uesed";

$con    =       mysqli_connect($host, $user, $pwd, $db);

# check connection
if (!$con){
//      echo "database not found". mysqli_error($con). "\n";
        die();
} else{
//      echo "connect succeeded";
}

?>  