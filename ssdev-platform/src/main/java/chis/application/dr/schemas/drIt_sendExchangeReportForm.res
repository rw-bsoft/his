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
					['like', ['$','c.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>