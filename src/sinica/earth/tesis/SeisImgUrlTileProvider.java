package sinica.earth.tesis;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.maps.model.UrlTileProvider;

public class SeisImgUrlTileProvider extends UrlTileProvider {

	public SeisImgUrlTileProvider() {
		super(256, 256);
	}

	@Override
	public URL getTileUrl(int x, int y, int zoom) {
		try {
			URL url = new URL("http://tesis.earth.sinica.edu.tw/testimage/seisMapTiles35_0_50/"+x+"-"+y+"-"+zoom+".png");
			return url;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
