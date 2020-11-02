package phis.application.reg.ws;

public class TestHyglWsService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HyglService hygl = new HyglService();
		StringBuffer reqParams = new StringBuffer();
		//挂号预约
		reqParams.append("{serviceId:'phis.hyglService',serviceAction:'saveYygh',data:{jgid:320111006,ksdm:1234,ysdm:1234,yyrq:2017-07-16,zblb:1,jzxh:1234,sbxh:1121,brid:320106201705054199}}");
		//取消挂号预约
//		reqParams.append("{serviceId:'phis.hyglService',serviceAction:'saveYyqx',data:{sbxh:1121,brid:320106201705054199}}");
		String res = hygl.execute(reqParams.toString());
		System.out.println(res);
	}

}
