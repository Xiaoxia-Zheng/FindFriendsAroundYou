<?php
require "init.php";
$username = $_POST["username"];
$password = $_POST["password"];

$sql_query = "select username from group29 where username like '$username' and $

$result = mysqli_query($con, $sql_query);

if (mysqli_num_rows($result) > 0) 
{
        $row = mysqli_fetch_assoc($result);
        $username = $row["username"];
        echo "Login Success!" ;
}
else
{
        echo "Username or Password incorrect!";
}



?>


