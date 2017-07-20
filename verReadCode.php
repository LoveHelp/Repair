<?php
header("content-type:text/html;charset=utf-8"); 
//文件路径 
$file_path="vercode.txt"; 
$conn = "";
if(file_exists($file_path)){    //判断要打开的txt文件是否存在
	$conn=file_get_contents($file_path); 
}
echo $conn; 
?>