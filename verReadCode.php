<?php
header("content-type:text/html;charset=utf-8"); 
//�ļ�·�� 
$file_path="vercode.txt"; 
$conn = "";
if(file_exists($file_path)){    //�ж�Ҫ�򿪵�txt�ļ��Ƿ����
	$conn=file_get_contents($file_path); 
}
echo $conn; 
?>