<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$bugid=$_POST["bugid"];

$q=mysql_query("SELECT * FROM bugrepair WHERE bugid=".$bugid);
	$result;
	$num_result = mysql_num_rows($q);
	if($num_result==1)
	{
		while($info=mysql_fetch_assoc($q))
		{
			//$result = json_encode($info);
			$rows[]=$info;
		}
		$result=json_encode($rows);

		$repairUserName="无";
		$repairuserid=$rows[0]['repairuserid'];
		if(is_array($rows) && $repairuserid){
			$repairUserName="";
			$s="SELECT personname FROM user WHERE userid in (" . $repairuserid.")";
			$r = mysql_query("$s");
			while($row1=mysql_fetch_assoc($r)){
				//echo $row1["personname"];
				$repairUserName.=$row1["personname"].","; 
			}
		}
		$result .= "+".$repairUserName; 
		
		echo $result;
	}

mysql_close();

?>
