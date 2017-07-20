<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$bugtype = isset($_POST["bugtype"])?$_POST["bugtype"]:"信号灯";
$sql=mysql_query("select * from part where bugtype = '".$bugtype."' ORDER BY convert(partname USING gbk) COLLATE gbk_chinese_ci asc");// 
		
if(mysql_num_rows($sql)>0){
	while($row1=mysql_fetch_assoc($sql)){
		$output[]=$row1; 
	}
	echo json_encode($output);
}		

mysql_close();

?>