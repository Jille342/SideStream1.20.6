package client.utils;

import client.Client;
import client.mixin.client.MinecraftClientAccessor;
import client.mixin.client.YggdrasilMinecraftSessionServiceAccessor;
import com.mojang.authlib.Environment;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import de.florianmichael.waybackauthlib.WaybackAuthLib;
import net.minecraft.client.session.Session;
import net.minecraft.network.encryption.SignatureVerifier;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.util.Optional;

public class AlteningUtils implements MCUtil{
    private static final Environment ENVIRONMENT = new Environment("http://sessionserver.thealtening.com", "http://authserver.thealtening.com", "The Altening");
    private static final YggdrasilAuthenticationService SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY, ENVIRONMENT);
    private @Nullable static WaybackAuthLib auth;




    public static void login(String s) {
        auth = getAuth(s);

        applyLoginEnvironment(SERVICE, YggdrasilMinecraftSessionServiceAccessor.createYggdrasilMinecraftSessionService(SERVICE.getServicesKeySet(), SERVICE.getProxy(), ENVIRONMENT));
        try {
            auth.logIn();
            setSession(new Session(auth.getCurrentProfile().getName(), auth.getCurrentProfile().getId(), auth.getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MOJANG));
        } catch (Exception ignored) {
        }
    }
    private static WaybackAuthLib getAuth(String s) {
        WaybackAuthLib auth = new WaybackAuthLib(ENVIRONMENT.servicesHost());
        auth.setUsername(s);
        auth.setPassword("aa");

        return auth;
    }
    public static void setSession(Session session) {
        Client.IMC.setSession(session);
    }


    public static void applyLoginEnvironment(YggdrasilAuthenticationService authService, MinecraftSessionService sessService) {
        MinecraftClientAccessor mca = (MinecraftClientAccessor) mc;
        mca.setAuthenticationService(authService);
        SignatureVerifier.create(authService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
        mca.setSessionService(sessService);
    }
}
