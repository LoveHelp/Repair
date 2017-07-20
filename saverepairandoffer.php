<?php
header("Content-type: text/html; charset=utf-8");

if(!empty($_FILES["file"])){
  foreach ($_FILES["file"]["error"] as $key => $error) {  
    $tmp_name = $_FILES["file"]["tmp_name"][$key];  
    $name    = $_FILES["file"]["name"][$key];
    $patch="./upload/baojiafile/".$name; 
    //move_uploaded_file($tmp_name, $patch);
		move_uploaded_file($tmp_name, iconv("UTF-8", "gb2312", $patch));
  } 
}

include_once 'conn.php';

$bugid=$_POST["bugid"];
$bigcharge=urldecode($_POST["charge"]);
$sqllist=urldecode($_POST["sqllist"]);
$chargefile=urldecode($_POST["chargefile"]);
//$sqllist="insert into offer(bugid,partid,num,price) values(8,2,2,7720.0),(8,2,1,1850.0)";//;insert into offer(bugid,partid,num,price) values(8,2,2,7720.0)
//$sqllist="insert into offer(bugid,partid,num,price,partname) values(8,0,1,888,'hhhh')";
$update="update bugrepair set state=4,bigcharge=".$bigcharge.",chargefile='".$chargefile."',chargetime=NOW() WHERE bugid=".$bugid;

//echo $sqllist;

$res = mysql_query($update);

if($res){
	if($sqllist!=""){
		$sql="delete from offer where bugid = ".$bugid;
		mysql_query($sql);
		mysql_query($sqllist);
	}
	echo "1";
}else{
	echo "0";
} 

mysql_close();

?>
