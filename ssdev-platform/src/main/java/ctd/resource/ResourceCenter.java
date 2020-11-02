package ctd.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import ctd.util.AppContextHolder;
import ctd.util.S;

public class ResourceCenter implements ResourceLoaderAware {
	private static ResourceLoader loader = new DefaultResourceLoader();
	private static RemoteResourceLoader remoteLoader;
	
	public static Resource load(String path) throws IOException{
		Resource r = loader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + path);
		if(r.exists()){
			return r;
		}
		r = loader.getResource(path);
		if(r.exists()){
			return r;
		}
		if(remoteLoader != null){
			return remoteLoader.load(path,!AppContextHolder.isDevMode());
		}
		else{
			throw new FileNotFoundException("file not found:" + path);
		}
	}
	
	public static Resource load(String pathPrefix,String path) throws IOException{
		Resource r =  loader.getResource(pathPrefix + path);
		if(r.exists()){
			return r;
		}
		else{
			r = loader.getResource(path);
			if(r.exists()){
				return r;
			}
			throw new FileNotFoundException("file not found:" + path);
		}
	}
	
	public static Resource load(String pathPrefix,String path,boolean cache) throws IOException{
		Resource r = loader.getResource(pathPrefix + path);
		if(r.exists()){
			if(!cache){
				r = loader.getResource(r.getURL().toString());
			}
			return r;
		}
		if(remoteLoader != null){
			return remoteLoader.load(path,!AppContextHolder.isDevMode());
		}
		else{
			throw new FileNotFoundException("file not found:" + path);
		}
		
	}
	
	@Override
	public void setResourceLoader(ResourceLoader appContextLoader) {
		loader = appContextLoader;
	}
	
	public void setRemoteResourceLoader(RemoteResourceLoader loader){
		remoteLoader = loader;
	}
	
	public static String getAbstractClassPath() throws URISyntaxException{
		return new File(loader.getClassLoader().getResource("").toURI()).getAbsolutePath();
	}
	
	public static String getAbstractClassPath(String path) throws URISyntaxException{
		return S.join(getAbstractClassPath(),"/",path);
	}
	
}
