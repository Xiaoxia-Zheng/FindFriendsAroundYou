<?php
#update the location information.
require "init.php";
$username = $_POST["l_username"];
$password = $_POST["l_password"];
$latitude = $_POST["username"];
$longitude = $_POST["password"];

$sql_query = "update group29 set latitude = '$latitude', longitude = '$longitud$

#echo $sql_query;
#$result = mysqli_query($con, $sql_query);

if(mysqli_query($con, $sql_query))
{
        echo "Success";
}
else
{
        echo "error ". mysqli_error($con);
    }


?>


