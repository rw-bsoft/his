<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 
				<filter action="query">
					['and',['eq',['$','a.JGID'],["$",'%user.manageUnit.id']],['eq',['$','d.YKSB'],["$",'%user.properties.storehouseId']]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>