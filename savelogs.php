<?php
header("Content-type: text/html; charset=utf-8");

if(!empty($_FILES["file"])){
		$tmp_name = $_FILES["file"]["tmp_name"];  
    $name    = $_FILES["file"]["name"];
    $patch="./log/".$name; 
		move_uploaded_file($tmp_name, iconv("UTF-8", "gb2312", $patch));

}

mysql_close();

?>
