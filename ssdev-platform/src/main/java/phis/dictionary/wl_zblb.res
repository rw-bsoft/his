<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 
				<filter action="query">
					['or',['and',['and',['eq',['$','a.JGID'],["$",'%user.properties.topUnitId']],['ne',['$','a.JGID'],["$",'%user.manageUnit.id']]],['eq',['$','ZBZT'],["i",1]]],['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>