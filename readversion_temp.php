<?php
header("content-type:text/html;charset=utf-8"); 
//�ļ�·�� 
$file_path="pc/version_temp.txt"; 
if(file_exists($file_path)){    //�ж�Ҫ�򿪵�txt�ļ��Ƿ����
	//$fp=fopen($file_path,"a+");
	$conn=file_get_contents($file_path); 
	echo $conn; 
	//fclose($fp);
}
?>