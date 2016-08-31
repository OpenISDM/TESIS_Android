package sinica.earth.tesis;

import com.google.android.gms.maps.model.UrlTileProvider;

import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;

public class myUrlTileProvider extends UrlTileProvider {

	Context ctx;

	public myUrlTileProvider(Context ctx) {
		super(256, 256);
		this.ctx = ctx;
	}

	@Override
	public URL getTileUrl(int x, int y, int zoom) {
		try {
			URL url = new URL("http://tesis.earth.sinica.edu.tw/testimage/imageAll/"+x+"-"+y+"-"+zoom+".png");
			return url;
		} catch (MalformedURLException e){
			e.printStackTrace();
		}
		return null;
	}

}
