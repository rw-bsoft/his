<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 
				<filter action="query">
					['eq',['$','a.XTSB'],["$",'%user.properties.storehouseId']]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>