<%@ page language="java"  pageEncoding="UTF-8"  %>
<html>
<head>
	<title>project</title>
	<!--<script type="text/javascript" src="js/mxclient.js"></script>-->
	<script type=text/javascript src="js/jquery-1.4.1.js"></script> 
	<style type="text/css">
	<!--
	#mainContainer {
	 float: left;
	 height: auto;
	 width: auto;
	 border: 1px solid #FFFFFF;
	}
	
	#info project {
		margin-left:50px;
		//text-indent:8mm;
	}
		
	#project sprint {
		margin-left:30px
	}		
	ul{
		margin-left:10px;  		
	}
  ul li{  
		margin:0;
		padding:0;		
		text-indent:0;  		
  	margin-left:10px;  	
  	font:bold 18px ;
  	font-color:red;
  }
  
  ul li ul li{
  	list-style-type:none;
  	font:16px "宋体"
  }
 
  
	#graph {
	 float: left;
	 height: 500px;
	 width: 720px;
	 border: 1px solid #000000;
	 overflow:hidden;
	 cursor:default;
	}
	#properties {
	 float: left;
	 height: 300px;
	 width: 160px;
	 margin-left:2px;
	 border: solid 1px black; 
	 padding: 5px;
	}
	.left_div {
	 border: 1px solid #999999;
	}
	-->
	</style>
	<script type="text/javascript">
		function toSprintPage(projectName){
			var de=document.compatMode=="CSS1Compat"?document.documentElement:document.body;
			$("#sprint").css("display","");  
			$("#sprint").css("left",(de.clientWidth-400)/2 +'px'); 
			$("#sprint").css("top",(de.clientHeight-280)/2 +'px'); 
			document.getElementById("sprintProjectName").value=projectName;			
		}
		function showInfo(){
			$.post("/project.do", jQuery.param({ handle: "getAllProject" }),
        function(data){ 
      		var json=$.parseJSON(data); 
      		$("#info").html("");
      		var projects=json;
      		var str="<ul>";
					$.each(projects,function(i){
							str+="<li>"+projects[i]['projectName']+"(<a href=\"javascript:toSprintPage('"+projects[i]['projectName']+"')\">"+"create sprint"+"</a>)";
					    var sprints=projects[i].sprints;
					    if(sprints){
					    	str+="<ul>";
						    $.each(sprints,function(j){
						    	str+="<li><a href=\"taskwall.jsp?handle=getTaskWall&projectName="+sprints[j]['projectName']+"&sprintName="+sprints[j]['sprintName']+"\">sprint#"+sprints[j]['sprintName']+"("+sprints[j]['startDate']+"~"+sprints[j]['endDate']+")</a></li>";
						    })
						    str+="</ul>";
					 	 }
					 	 str+="</li>";					    
					});
					str+="</ul>";  
					$("#info").html(str);

  
      });			
		}
		$(document).ready(
		    function(){
		    	showInfo();          
		      $("#createBtn").click(
		         function(){  
		            var projName=jQuery.trim(document.getElementById("projectName").value);
		            if(projName==""){
		            	alert("必须输入项目名称");                	
		            }else{				        
									$.post("/project.do", jQuery.param({ handle: "createProject" ,projectName: projName}),
					          function(data){ 
				          		var json=$.parseJSON(data); 
				          		if(json.result!="ok"){
				          			alert(json.result);
				          		}else{
				          			showInfo();
				          		}    
					        });						                         	
		            }        
		         }//end function
		      );
		      $("#cancelBtn").click(
		         function(){  
		            $("#sprint").css("display","none");       
		         }//end function
		      );	
		      
					$("#createSprintBtn").click(
		         function(){  
		            var startDate=jQuery.trim(document.getElementById("startDate").value);
		            var endDate=jQuery.trim(document.getElementById("endDate").value);
		            var projectName=jQuery.trim(document.getElementById("sprintProjectName").value);
		            if(startDate==""){
		            	alert("必须输入开始时间");                	
		            }else if(endDate==""){
		            	alert("必须输入结束时间");                	
		            }else{				        
									$.post("/project.do", jQuery.param({ handle: "createSprint" ,projectName:projectName,startDate: startDate,endDate:endDate}),
					          function(data){ 
				          		var json=$.parseJSON(data); 
				          		if(json.result!="ok"){
				          			alert(json.result);
				          		}else{
				          			showInfo();
				          			$("#sprint").css("display","none"); 
				          		}    
					        });						                         	
		            }        
		         }//end function
		      );		      	      
		   }
		); 		
  
  
	</script>	
</head>

<!-- Page passes the container for the graph to the grogram -->
<body >

<div id="mainContainer">
	<div id="create"><input type=text name=projectName id=projectName/><input type=button value="create a project" id=createBtn></div>
	<div id="info"></div>
  <div id="sprint" style="position:fixed;_position:absolute;z-index:1000;width:400px;height:280px;background:#FFFFFF;border:solid 1px #09c;display:none">
		<p>Sprint info edit...</p>
		<p>startDate:<input type=text name=startDate id=startDate/></p>
		<p>endDate:<input type=text name=endDate id=endDate/>		</p>
		<p>
			<input type=button value="create a sprint" id=createSprintBtn> 
			<input type=button value="cancel" id=cancelBtn>
			<input type=hidden id=sprintProjectName>
		</p>  	
  </div>
  
</div>			
</body>

<!--<textarea id=allHtml></textarea>-->
</html>