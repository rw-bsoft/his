<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>	
				<filter action="query">
						['eq', ['$','a.createUser'],['$','%user.userId']]
				</filter>				 		
			</conditions>
		</p>
		<p principal="Tsystem" mode="15">
			<conditions>			 		
			</conditions>
		</p>
	</permissions>
</entity>