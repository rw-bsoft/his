<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="T04" mode="15">
			<conditions>			 
				<filter action="query">
					['like',['$','a.createUnit'],['concat',['substring',['$','%user.manageUnit.id'], 0,9],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T06" mode="15">
			<conditions>			 
				<filter action="query">
					['like',['$','a.createUnit'],['concat',['substring',['$','%user.manageUnit.id'], 0,9],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T08" mode="15">
			<conditions>			 
				<filter action="query">
					['like',['$','a.createUnit'],['concat',['substring',['$','%user.manageUnit.id'], 0,9],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T09" mode="15">
			<conditions>			 
				<filter action="query">
					['like',['$','a.createUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T10" mode="15">
			<conditions>			 
				<filter action="query">
					['like',['$','a.createUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T13" mode="15">
			<conditions>			 
				<filter action="query">
					['like',['$','a.createUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['like',['$','a.createUnit'],['concat',['substring',['$','%user.manageUnit.id'], 0,9],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="$Others" mode="15">
			<conditions>			 
			</conditions>
		</p>
	</permissions>
</entity>