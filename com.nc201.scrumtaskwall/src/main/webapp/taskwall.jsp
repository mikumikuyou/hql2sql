<%@ page language="java"  pageEncoding="UTF-8"  %>
<%
	String projectName=request.getParameter("projectName");
	String sprintName=request.getParameter("sprintName");
	String taskDate=request.getParameter("taskDate");
	
	projectName=projectName==null||"".equals(projectName)?"":projectName;
	sprintName=sprintName==null||"".equals(sprintName)?"":sprintName;
	taskDate=taskDate==null||"".equals(taskDate)?"":taskDate;
%>

<html>
<head>
	<title>任务墙</title>

	<style type="text/css">
	<!--
	#mainContainer {
	 float: left;
	 height: auto;
	 width: auto;
	 border: 1px solid #FFFFFF;
	}
	#graph {
	 float: left;
	 height: 560px;
	 width: 720px;
	 border: 1px solid #000000;
	 overflow:hidden;
	 cursor:default;
	 background: url('images/grid.gif');
	}
	#properties {
	 float: left;
	 height: 300px;
	 width: 160px;
	 margin-left:2px;
	 border: solid 1px black; 
	 padding: 5px;
	 font:12px "宋体";
	}
	
	#properties td {
	 font:12px "宋体";
	 white-space: nowrap;
	}	

	-->
	</style>
	
	<!-- Sets the basepath for the library if not in same directory -->
	<script type="text/javascript">
		mxBasePath = './';
	</script> 

	<!-- Loads and initializes the library -->
	<script type="text/javascript" src="js/mxclient.js"></script>
	<script type=text/javascript src="js/jquery-1.4.1.js"></script> 
	<script language="JavaScript" src="FusionChartsFree/JSClass/FusionCharts.js"></script>	
	<!-- Example code -->
	<script type="text/javascript">
		/**
		* loading层
		*/
		var hideSplash = function(){
			// Fades-out the splash screen
			var splash = document.getElementById('splash');
			
			if (splash != null){
				try{
					mxEvent.release(splash);
					mxEffects.fadeOut(splash, 100, true);
				}catch (e){
					splash.parentNode.removeChild(splash);
				}
			}
		}
		/**
		* 页面onload入口
		*/				
		function main(container){

			try{
				if (!mxClient.isBrowserSupported()){
					mxUtils.error('Browser is not supported!', 200, false);
				}else{
					var node = mxUtils.load("config/layouteditor.xml").getDocumentElement();
					var editor = new mxEditor(node);
					editor.setGraphContainer(container);
	
					// Shows the application
					hideSplash();
					
					var graph=editor.graph;
					//cell是否允许移动
					graph.isCellMovable = function(cell){
						return !graph.isSwimlane(cell);
					};	
					//是否允许改变cell大小
					graph.isCellResizable = function(cell){
						return false;
					};
					
					//是否允许折叠
					graph.isCellFoldable = function(cell, collapse){
						return false;
					};
					
					//是否允许撑大父容器		
					graph.isExtendParent = function(cell){
						return false;
					};		
					
					graph.graphHandler.setRemoveCellsFromParent(false);
					//是否允许编辑cell
					graph.isCellEditable = function(cell){
						return false;
					};		
					
					//是否允许编辑cell
					graph.isCellDeletable = function(cell){
						return !graph.isSwimlane(cell);
					};		
					graph.setDropEnabled(true);				
				
				
					//任务主标签
					graph.getLabel = function(cell){
						var label = (this.labelsVisible) ?
							this.convertValueToString(cell) : '';
						var geometry = this.model.getGeometry(cell);
						
						if (!this.model.isCollapsed(cell) &&
							geometry != null &&
							(geometry.offset == null ||
							(geometry.offset.x == 0 &&
							geometry.offset.y == 0)) &&
							this.model.isVertex(cell) &&
							geometry.width >= 2){
							var style = this.getCellStyle(cell);
							var fontSize = style[mxConstants.STYLE_FONTSIZE] ||
								mxConstants.DEFAULT_FONTSIZE;
							var max = geometry.width / (fontSize * 0.625);
							if (max < (!label||label==null?0:countLength(label))){
								return label.substring(0, max/2) + '...';
							}
						}
						return label;
					};
					
					// Hook for returning shape number for a given cell
					graph.getSecondLabel = function(cell){
						if (!graph.isSwimlane(cell)){
							if(cell.getAttribute("status")=="notChecked"){
								return cell.getAttribute("initDay")+"";
							}else if(cell.getAttribute("status")=="checked"){
								return cell.getAttribute("remainDay")+"";
							}else if(cell.getAttribute("status")=="done"){
								return cell.getAttribute("initDay")+"";
							}							
						}else{
							var parentLabel=cell.value;
								var totalday=0;
								if(cell.children&&cell.children!=null){
									for(var i=0;i<cell.children.length;i++){
											if(parentLabel=="NOT CHECKED OUT"||parentLabel=="DONE!!!"){
												totalday+=parseInt(cell.children[i].getAttribute("initDay"));
											}else{
												totalday+=parseInt(cell.children[i].getAttribute("remainDay"));
											}
									}
								}
								return totalday+"";
						}
					};
					graph.getThirdLabel = function(cell){
						if (!graph.isSwimlane(cell)){
							return cell.getAttribute("charge")==null?"":cell.getAttribute("charge");
						}
						
					};							
					var secondLabelVisible=true;
					var createShape = graph.cellRenderer.createShape;
					graph.cellRenderer.createShape = function(state){
						createShape.apply(this, arguments);					
						if (!state.cell.geometry.relative){
							var secondLabel = graph.getSecondLabel(state.cell);
							var thirdLabel = graph.getThirdLabel(state.cell);
							if (secondLabel != null &&state.shape != null &&state.secondLabel == null){
								state.secondLabel = new mxText(secondLabel, new mxRectangle(),
										mxConstants.ALIGN_LEFT, mxConstants.ALIGN_BOTTOM);
	
								// Styles the label
								state.secondLabel.color = 'black';
								state.secondLabel.family = 'Verdana';
								state.secondLabel.size = 12;
								state.secondLabel.fontStyle = mxConstants.FONT_ITALIC;
								state.secondLabel.background = 'yellow';
								state.secondLabel.border = 'black';
	
								state.secondLabel.dialect = state.shape.dialect;
								state.secondLabel.init(state.view.getDrawPane());
							}
								
							if (thirdLabel != null &&state.shape != null &&state.thirdLabel == null){
								state.thirdLabel = new mxText("test", new mxRectangle(),
										mxConstants.ALIGN_LEFT, mxConstants.ALIGN_BOTTOM);
	
								// Styles the label
								state.thirdLabel.color = 'black';
								state.thirdLabel.family = 'Verdana';
								state.thirdLabel.size = 12;
								state.thirdLabel.fontStyle = mxConstants.FONT_ITALIC;
								state.thirdLabel.background = '#FFCACA';
								state.thirdLabel.border = 'black';
	
								state.thirdLabel.dialect = state.shape.dialect;
								state.thirdLabel.init(state.view.getDrawPane());							
							}
						}
					};				
					
					// Redraws the shape number after the cell has been moved/resized
					var redraw = graph.cellRenderer.redraw;
					graph.cellRenderer.redraw = function(state){
						redraw.apply(this, arguments);
						var scale = graph.getView().getScale();
						if (state.shape != null &&state.secondLabel != null){
							
						
							//var bounds = new mxRectangle(state.x + state.width - 8 * scale, state.y + 8 * scale, 0, 0);
							var bounds = null;
							if(graph.isSwimlane(state.cell)){
								bounds = new mxRectangle(state.x+ state.width - 30 * scale, state.y + 24 * scale, 0, 0);
							}else{
								bounds = new mxRectangle(state.x - 8 * scale, state.y + 8 * scale, 0, 0);
							}
							
							state.secondLabel.value = graph.getSecondLabel(state.cell);
							state.secondLabel.scale = scale;
							state.secondLabel.bounds = bounds;
							state.secondLabel.redraw();
						}
						if (state.shape != null && state.thirdLabel != null){
							var bounds2 = new mxRectangle(state.x + state.width - 8 * scale, state.y + 8 * scale+state.cell.geometry.height, 0, 0);
							//var bounds2 = new mxRectangle(state.x - 8 * scale, state.y + 10 * scale+state.cell.geometry.height+5, 0, 0);
							state.thirdLabel.value = graph.getThirdLabel(state.cell);
							state.thirdLabel.scale = scale;
							state.thirdLabel.bounds = bounds2;
							state.thirdLabel.redraw();
							//alert("重画task");						
							
						}
					};		
					
					// Destroys the shape number
					var destroy = graph.cellRenderer.destroy;
					graph.cellRenderer.destroy = function(state){
						destroy.apply(this, arguments);					
						if (state.secondLabel != null){
							state.secondLabel.destroy();
							state.secondLabel = null;
						}
						if (state.thirdLabel != null){	
							state.thirdLabel.destroy();
							state.thirdLabel = null;						
						}
					};									
				

					graph.getSelectionModel().addListener(mxEvent.CHANGE, function(sender, evt){
						selectionChanged(graph);
					});
		


					var applyCellChange= function(evt){
						var cells = evt.getProperty('cells');
						//alert("xxx:"+cells.length);
						for(var i=0;i<cells.length;i++){
							var parentLabel=cells[i].parent.value;
							if(parentLabel=="NOT CHECKED OUT"){
								cells[i].setAttribute("remainDay",cells[i].getAttribute("initDay"));
								cells[i].setAttribute("status","notChecked");
							}else if(parentLabel=="CHEKCED OUT"){
								cells[i].setAttribute("remainDay",cells[i].getAttribute("initDay"));
								cells[i].setAttribute("status","checked");
							}else if(parentLabel=="DONE!!!"){
								cells[i].setAttribute("remainDay","0");
								cells[i].setAttribute("status","done");
							}								
						}
						
					};
					graph.addListener(mxEvent.MOVE_CELLS, function(sender, evt){
						applyCellChange(evt);
					});	
					
					graph.addListener(mxEvent.CELLS_ADDED, function(sender, evt){
						applyCellChange(evt);
					});	
					selectionChanged(graph);					
					graph.setEnabled(false);	
					document.getElementById("taskBtn").style.display="none"; 
					document.getElementById("autoBtn").style.display="none"; 
					document.getElementById("selectBtn").style.display="none"; 								
					doInit(editor);	
					
					
					graph.createHandler = function(state){
						if (state != null &&
							!this.isSwimlane(state.cell)){
							return new mxVertexToolHandler(state);
						}
	
						return mxGraph.prototype.createHandler.apply(this, arguments);
					};


					burnChart();

													
				}
			}catch (e){
				hideSplash();
				// Shows an error message if the editor cannot start
				mxUtils.alert('Cannot start application: '+e.message);
				throw e; // for debugging
			}
		};
		
		function burnChart(){
			//燃尽图	
			 var chart = new FusionCharts("FusionChartsFree/Charts/FCF_MSLine.swf", "ChartId", "800", "400");
			 //var chart = new FusionCharts("FusionChartsFree/Charts/FCF_MSArea2D.swf", "ChartId", "800", "400");
		   chart.setDataURL(escape("project.do?handle=getBurndown&projectName=<%=projectName%>&sprintName=<%=sprintName%>&taskDate=<%=taskDate%>"));
		   chart.render("chartdiv");			
		}
		function autoArrange(editor){
			hideSplash();
			var notChecked=editor.graph.model.getRoot().children[0].children[0];
			var checked=editor.graph.model.getRoot().children[0].children[1];
			var done=editor.graph.model.getRoot().children[0].children[2];
			
			var applyArrange=function(graph,cell){
				var totalRow=cell.getChildCount()/2;
				if(totalRow>0){
					for(var i=0;i<cell.getChildCount();i++){
						
						var hSpace=(cell.geometry.height-23)/totalRow;
						hSpace=hSpace>40?40:hSpace;
						var wSpace=cell.geometry.width/2;						
						
						var y=(Math.ceil((i+1)/2)-1)*hSpace+23;
						var x=(i%2)>0?cell.geometry.width/2+5:5;

						cell.children[i].geometry.x=x;
						cell.children[i].geometry.y=y;		

					
					}
					graph.refresh();
				}
				
				
			};
			applyArrange(editor.graph,notChecked);
			applyArrange(editor.graph,checked);
			applyArrange(editor.graph,done);
			hideSplash();
	
		}
		
		
		function mxVertexToolHandler(state){
			mxVertexHandler.apply(this, arguments);
		};

		mxVertexToolHandler.prototype = new mxVertexHandler();
		mxVertexToolHandler.prototype.constructor = mxVertexToolHandler;

		mxVertexToolHandler.prototype.domNode = null;

		mxVertexToolHandler.prototype.init = function(){
			mxVertexHandler.prototype.init.apply(this, arguments);

			// In this example we force the use of DIVs for images in IE. This
			// handles transparency in PNG images properly in IE and fixes the
			// problem that IE routes all mouse events for a gesture via the
			// initial IMG node, which means the target vertices 
			
			this.domNode = document.createElement('div');
			this.domNode.style.position = 'absolute';
			this.domNode.style.whiteSpace = 'nowrap';
			var md = (mxClient.IS_TOUCH) ? 'touchstart' : 'mousedown';

			// Delete
			var img = mxUtils.createImage('images/delete2.png', true);
			img.style.cursor = 'pointer';
			img.style.width = '16px';
			img.style.height = '16px';
			mxEvent.addListener(img, md,
				mxUtils.bind(this, function(evt){
					// Disables dragging the image
					mxEvent.consume(evt);
				})
			);
			mxEvent.addListener(img, 'click',
				mxUtils.bind(this, function(evt){
					this.graph.removeCells([this.state.cell]);
					mxEvent.consume(evt);
				})
			);
			this.domNode.appendChild(img);

			
			this.graph.container.appendChild(this.domNode);
			this.redrawTools();
		};

		mxVertexToolHandler.prototype.redraw = function(){
			mxVertexHandler.prototype.redraw.apply(this);
			this.redrawTools();
		};

		mxVertexToolHandler.prototype.redrawTools = function(){
			if (this.state != null && this.domNode != null){
				this.domNode.style.left = (this.state.x + this.state.width-10 ) + 'px';
				this.domNode.style.top = (this.state.y + this.state.height -34) + 'px';
			}
		};
		
		mxVertexToolHandler.prototype.destroy = function(sender, me){
			mxVertexHandler.prototype.destroy.apply(this, arguments);
			if (this.domNode != null){
				this.domNode.parentNode.removeChild(this.domNode);
				this.domNode = null;
			}
		};		
		
		
			//alert(countLength("中英文混合!abcd"));
			function countLength(stringToCount){ 
	     //計算有幾個全型字、中文字...  
	     var c = stringToCount.match(/[^ -~]/g);  
	     return stringToCount.length + (c ? c.length : 0);  
			}
			/**
			 * Updates the properties panel
			 */
			function selectionChanged(graph){
				var div = document.getElementById('properties');

				// Forces focusout in IE
				graph.container.focus();

				// Clears the DIV the non-DOM way
				div.innerHTML = '';

				// Gets the selection cell
				var cell = graph.getSelectionCell();

				if (cell == null){
					mxUtils.writeln(div, 'Please select a task.');
				}else if(!graph.isSwimlane(cell)){
					// Creates the form from the attributes of the user object
					var form = new mxForm();
				
					var attrs = cell.value.attributes;
					
					
					for (var i = 0; i < attrs.length; i++){
						if(attrs[i].nodeName!="status"){
							createTextField(graph, form, cell, attrs[i]);
						}
					}
					div.appendChild(form.getTable());
					//mxUtils.br(div);
				}
			}

			/**
			 * Creates the textfield for the given property.
			 */
			function createTextField(graph, form, cell, attribute){
				var nodeName=attribute.nodeName;
				if(attribute.nodeName=="title"){
					nodeName="任务描述";
				}else if(attribute.nodeName=="initDay"){
					nodeName="总工作量";
				}else if(attribute.nodeName=="remainDay"){
					nodeName="剩余工作量";					
				}else if(attribute.nodeName=="charge"){
					nodeName="负责人";
				}
				
				var input = form.addText(nodeName + ':', attribute.nodeValue);
				
				//if(cell.parent=="NOT CHECKED OUT"
				var parentLabel=cell.parent.value;
				if(parentLabel=="NOT CHECKED OUT"){
					if(attribute.nodeName=="remainDay"){
						input.disabled=true;
					}					
				}else if(parentLabel=="CHEKCED OUT"){
					if(attribute.nodeName=="initDay"){
						input.disabled=true;
					}
				}else if(parentLabel=="DONE!!!"){
					input.disabled=true;
				}			

				var applyHandler = function(){
					var newValue = input.value || '';
					var oldValue = cell.getAttribute(attribute.nodeName, '');

					if (newValue != oldValue){
						graph.getModel().beginUpdate();
                        
            try{
            	var edit = new mxCellAttributeChange(cell, attribute.nodeName,newValue);
            	var edit2 = new mxCellAttributeChange(cell.parent,"value", cell.parent.value);
              graph.getModel().execute(edit);
              graph.getModel().execute(edit2);
            }finally{
              graph.getModel().endUpdate();
            }
					}
				}; 
				if (mxClient.IS_IE){
					mxEvent.addListener(input, 'focusout', applyHandler);
				}else{
					mxEvent.addListener(input, 'blur', applyHandler);
				}
			}		
			
			function lock(editor){
				hideSplash();
				editor.graph.clearSelection();
				var curEle=event.srcElement;
				if(curEle.id=="lock"){
					$.post("/project.do", jQuery.param({ handle: "lockTaskWall" ,projectName: "<%=projectName%>",sprintName:"<%=sprintName%>",taskDate:taskDate}),
	          function(data){ 	          		
		            var result=$.parseJSON(data).result;
		            if(result=="ok"){
		            	mxUtils.alert("锁定成功");
									curEle.id="unlock";
									curEle.src="images/unlock.gif";
									editor.graph.setEnabled(true);	
									document.getElementById("taskBtn").style.display=""; 
									document.getElementById("autoBtn").style.display="";         	
		            }else{
		            	mxUtils.error(result,result.length<200?200:result.length,true);
		            }	            
	        });				 
				}else if(curEle.id=="unlock"){					
					var encoder = new mxCodec();
					var node = encoder.encode(editor.graph.getModel());
					var taskXml=mxUtils.getXml(node);
					$.post("/project.do", jQuery.param({ handle: "unlockTaskWall" ,projectName: "<%=projectName%>",sprintName:"<%=sprintName%>",taskDate:taskDate,taskXml:taskXml}),
	          function(data){ 
		            var result=$.parseJSON(data).result;
		            if(result=="ok"){
		            	mxUtils.alert("解锁成功");
									curEle.id="lock";
									curEle.src="images/lock.gif";
									editor.graph.setEnabled(false); 	
									document.getElementById("taskBtn").style.display="none"; 
									document.getElementById("autoBtn").style.display="none"; 													            	
		            }else{
		            	mxUtils.error(result,result.length<200?200:result.length,true);
		            }	
		            burnChart();            
	        });		
	        					

				}		
				
				hideSplash();		
			} 
			
			function toXls(editor){
	      location.href="/project.do?handle=toXls&projectName=<%=projectName%>&sprintName=<%=sprintName%>&taskDate="+taskDate;  
			}		
			function importXls(editor){
				
			}
			var taskDate=null;
			function doInit(editor){
				$.post("/project.do", jQuery.param({ handle: "getTaskWall",projectName: "<%=projectName%>",sprintName:"<%=sprintName%>",taskDate:"<%=taskDate%>"}),
          function(data){
          		var json=$.parseJSON(data); 
          		taskDate=json.taskDate;
							var doc = mxUtils.parseXml(json.taskXml);
							var codec = new mxCodec(doc);
							codec.decode(doc.documentElement, editor.graph.getModel());
							
							$("#pageInfo").html("当前日期："+taskDate+" 前一天 后一天");
         });
				
				
			}
			
			
	</script>
</head>

<!-- Page passes the container for the graph to the grogram -->
<body onload="main(document.getElementById('graph'))">

<div id="mainContainer">
	<div id="pageInfo"></div>
	<div id="toolbar"></div>
	<div id="graph">
		<center id="splash" style="padding-top:230px;z-index:00">
			<img src="images/loading.gif">
		</center>		
	</div>
  <div id="properties" ></div>
  <div id="chartdiv" ></div>
</div>			
</body>

<!--<textarea id=allHtml></textarea>-->
</html>