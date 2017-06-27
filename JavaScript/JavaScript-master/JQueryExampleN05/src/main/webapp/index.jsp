<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="js/jquery-1.7.1.js"></script>
		<style type="text/css">			

		</style>
		<script type="text/javascript">
			$(function(){
//				$('#add').click(function(){
//					child = $('<p>This is a text of paragraph</p>');
//					$('#container').append(child);
//				});
//				
//				$('#add').click(function(){
//					child = $('<p>This is a text of paragraph</p>').hide();
//					$('#container').append(child);
//					child.fadeIn(1000);
//				});
//				
//				$('#add').click(function(){
//					child = $('<p>This is a text of paragraph</p>').hide();
//					$('#container').append(child);
//					child.fadeIn(3000, function(){
//						var currCount = $('#count').text();
//						currCount++;
//						$('#count').text(currCount);
//					});
//				});
//				
				$('#add').click(function(){
					child = $('<p>This is a text of paragraph</p>').hide();
					$('#container').append(child);
					child.fadeIn(1000, function(){
						var count = $('#count');
						var currCount = count.text();
						currCount++;
						count.fadeOut('fast', function(){
							count.text(currCount);
							count.fadeIn('fast');
						});
					});
				});
			})
		</script>
    </head>
    <body>
		<button id="add">add a paragraph</button>
		Добавлено <span id="count">0</span> параграфов
		<div id="container">
			
		</div>
    </body>
</html>
