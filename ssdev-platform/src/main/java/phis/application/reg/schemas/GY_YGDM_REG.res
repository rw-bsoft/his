<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 
				<filter action="query">
					['eq',['$','a.ORGANIZCODE'],["$",'%user.manageUnit.ref']]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>