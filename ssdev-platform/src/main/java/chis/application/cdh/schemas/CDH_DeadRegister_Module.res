<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="T01" mode="15">
			<conditions>			 
				<filter action="query">
					['eq',['$','b.manaDoctorId'],["$",'%user.properties.refUserId']]
				</filter>			
			</conditions>
		</p>
		<p principal="post" mode="15">
			<conditions>			 
				<filter action="query">
					['eq',['$','b.manaDoctorId'],["$",'%user.properties.refUserId']]
				</filter>			
			</conditions>
		</p>
		<p principal="$Others" mode="15">
			<conditions>			 
			</conditions>
		</p>
	</permissions>
</entity>