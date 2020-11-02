<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['eq',['$','a.ehrjgdm'],["$",'%user.manageUnit.id']]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>