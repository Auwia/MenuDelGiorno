package it.app.menudelgiorno.menudelgiorno.v2.googlemaps;

import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class PoisManager {
    private static int nSelected = 30;
    private static Vector<Poi> pois;
    private static Location selectedLocation;
    private static Poi selectedPoi;
    private static int selectedPoiIndex = -1;
    private static final HashMap<String, Integer> tradeImages = new HashMap<String, Integer>();

    private static final String SCARICA_LOCALI = "http://menudelgiornomecc.altervista.org/select_locali.php";

    public static void addPoi(Poi paramPoi) {
        if (pois == null)
            pois = new Vector<Poi>();
        pois.add(paramPoi);
    }

    public static void clear() {
        setSelectedPoiIndex(-1);
        if (pois != null)
            pois.clear();
    }

    public static int getSize() {
        if (pois != null)
            return pois.size();
        else
            return -1;
    }

    public static Poi getPoi(int paramInt) {
        if ((paramInt >= 0) && (paramInt < pois.size()))
            return pois.elementAt(paramInt);
        return null;
    }

    public static Location getSelectedLocation() {
        return selectedLocation;
    }

    public static Poi getSelectedPoi() {
        return selectedPoi;
    }

    public static int getSelectedPoiIndex() {
        return selectedPoiIndex;
    }

    public static int getnSelected() {
        return nSelected;
    }

    public static int nPois() {
        if (pois == null)
            return -1;
        return pois.size();
    }

    public static void readPoi(Context paramContext) {
        PoiAddress localPoiAddress;

        setSelectedPoiIndex(-1);
        tradeImages.put("Ristorante", R.drawable.ristorante);
        tradeImages.put("Fast Food", R.drawable.fastfood);
        tradeImages.put("Pub", R.drawable.pub);
        tradeImages.put("Pizzeria", R.drawable.pizzeria);
        tradeImages.put("Trattoria", R.drawable.pub);

        try {
            JSONArray jsonArray = new JSONArray(Utility.getWebServerResponse(SCARICA_LOCALI));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                localPoiAddress = new PoiAddress();
                localPoiAddress.putComponent("indirizzo",
                        jsonObject.getString("indirizzo"));
                localPoiAddress
                        .putComponent("cap", jsonObject.getString("cap"));
                localPoiAddress.putComponent("provincia",
                        jsonObject.getString("provincia"));
                localPoiAddress.putComponent("regione",
                        jsonObject.getString("regione"));
                localPoiAddress.putComponent("nazione",
                        jsonObject.getString("nazione"));

                Poi localPoi = new Poi(jsonObject.getDouble("latitudine"),
                        jsonObject.getDouble("longitudine"),
                        jsonObject.getInt("id"));
                localPoi.setDescription(jsonObject.getString("descrizione"));
                localPoi.setNomeLocale(jsonObject.getString("nome_locale"));
                localPoi.setTel(jsonObject.getString("telefono"));
                localPoi.setTrade(jsonObject.getString("tipologia"));
                localPoi.setAddress(localPoiAddress);

                if (tradeImages.containsKey(jsonObject.getString("tipologia")))
                    localPoi.setResourseIconId(tradeImages
                            .get(jsonObject.getString("tipologia")).intValue());
                addPoi(localPoi);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setDistances() {
        if ((selectedLocation != null) && (pois != null))
            ;
        for (int i = 0; ; i++) {
            if (i >= pois.size())
                return;
            pois.get(i).setDistance(selectedLocation
                    .distanceTo(pois.get(i).getLocation()));
        }
    }

    public static void setSelectedLocation(Location paramLocation) {
        setSelectedPoiIndex(-1);
        selectedLocation = paramLocation;
        setDistances();
        Collections.sort(pois);
    }

    public static void setSelectedPoi(Poi paramPoi) {
        selectedPoi = paramPoi;
        selectedPoiIndex = pois.indexOf(paramPoi);
    }

    public static void setSelectedPoiIndex(int paramInt) {
        selectedPoiIndex = paramInt;
        if (paramInt != -1) {
            setSelectedPoi(pois.elementAt(paramInt));
            return;
        }
        selectedPoi = null;
    }

    public static void setnSelected(int paramInt) {
        nSelected = paramInt;
    }
}