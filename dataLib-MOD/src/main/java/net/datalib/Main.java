import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FeatureFlagMod implements ModInitializer {
    public static final String MOD_ID = "feature_flag_mod";
    private static final Gson GSON = new Gson();

    // Kaydedilen flag'leri tutacağımız statik harita
    public static final Map<Identifier, Boolean> REGISTERED_FLAGS = new HashMap<>();

    @Override
    public void onInitialize() {
        // Sunucu veri paketlerini dinlemek için Resource Reload Listener ekliyoruz
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                
                @Override
                public Identifier getFabricId() {
                    return Identifier.of(MOD_ID, "datapack_feature_reader");
                }

                @Override
                public void reload(ResourceManager manager) {
                    REGISTERED_FLAGS.clear(); // Yeniden yüklemede eski flag'leri temizle

                    // Veri paketlerindeki namespace yollarında bulunan dosyaları arıyoruz
                    manager.findResources("data_packs", path -> path.getPath().endsWith(".json"))
                        .forEach((id, resource) -> {
                            try (InputStream stream = resource.getInputStream()) {
                                JsonObject json = GSON.fromJson(new InputStreamReader(stream), JsonObject.class);

                                // İlgili dosyanın namespace değerini alıyoruz
                                String namespace = id.getNamespace();
                                
                                // JSON içerisindeki değerleri okuyoruz
                                if (json.has("flag_name") && json.has("enabled")) {
                                    String flagName = json.get("flag_name").getAsString();
                                    boolean isEnabled = json.get("enabled").getAsBoolean();

                                    Identifier flagId = Identifier.of(namespace, flagName);
                                    
                                    // Flag'i sisteme register ediyoruz (belleğe kaydediyoruz)
                                    REGISTERED_FLAGS.put(flagId, isEnabled);
                                    System.out.println("Feature Flag Register Edildi: " + flagId + " = " + isEnabled);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                }
            }
        );
    }
}
