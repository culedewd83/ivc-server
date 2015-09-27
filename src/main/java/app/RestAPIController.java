package app;

import java.util.ArrayList;
import java.util.List;

import models.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utils.JsonHelper;
import utils.TextEncryptor;


@RestController
public class RestAPIController {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public BasicResponse createProfile(@RequestParam(value="data") String data) {
        BasicResponse response = new BasicResponse();
        response.success = false;

        String json;
        try {
            json = TextEncryptor.decrypt(data);
        } catch (Exception e) {
            response.message = "Error occurred creating profile\nCode: 001";
            return response;
        }

        ProfileInfo info;
        try {
            info = JsonHelper.getInstance().json.fromJson(json, ProfileInfo.class);
        } catch (Exception e) {
            response.message = "Error occurred creating profile\nCode: 002";
            return response;
        }

        if (info == null) {
            response.message = "Error occurred creating profile\nCode: 003";
            return response;
        }

        boolean hasRequiredInfo = true;

        response.message = "";
        if (nullOrWhitespace(info.aNumber)) {
            response.message = "A-number must not be blank\n";
            hasRequiredInfo = false;
        }

        if (nullOrWhitespace(info.email)) {
            response.message += "Email must not be blank\n";
            hasRequiredInfo = false;
        }

        if (nullOrWhitespace(info.name)) {
            response.message += "Name must not be blank\n";
            hasRequiredInfo = false;
        }

        if (!hasRequiredInfo) {
            return response;
        }

        List<Profile> profiles = Storage.getInstance().getProfiles();

        for(Profile p : profiles) {
            if (p != null && p.aNumber != null && p.aNumber.equals(info.aNumber)) {
                response.message = "Profile already exists";
                return response;
            }
        }

        Profile newProfile = new Profile();
        newProfile.aNumber = info.aNumber;
        newProfile.email = info.email;
        newProfile.name = info.name;
        newProfile.groups = new ArrayList<TemplateGroup>();

        profiles.add(newProfile);
        Storage.getInstance().saveProfiles();

        response.success = true;
        response.message = "Profile Created";

        return response;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ProfileResponse getProfile(@RequestParam(value="data") String data) {
        ProfileResponse response = new ProfileResponse();
        response.success = false;

        String key;
        try {
            key = TextEncryptor.decrypt(data);
        } catch (Exception e) {
            response.message = "Error occurred retrieving profile\nCode: 001";
            return response;
        }

        List<Profile> profiles = Storage.getInstance().getProfiles();
        for(Profile p : profiles) {
            if (p != null && p.aNumber != null && p.aNumber.equals(key)) {
                response.success = true;
                response.message = "success";
                response.data = TextEncryptor.encrypt(JsonHelper.getInstance().json.toJson(p));
                return response;
            }
        }

        response.message = "Profile not found";
        return response;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public BasicResponse saveProfile(@RequestParam(value="data") String data) {
        BasicResponse response = new BasicResponse();
        response.success = false;

        Profile profile;
        String json = "";

        try {
            json = TextEncryptor.decrypt(data);
        } catch (Exception e) {
            response.message = "Error occurred saving profile\nCode: 001";
            return response;
        }

        if (nullOrWhitespace(json)) {
            response.message = "Error occurred saving profile\nCode: 002";
            return response;
        }

        try {
            profile = JsonHelper.getInstance().json.fromJson(json, Profile.class);
        } catch (Exception e) {
            response.message = "Error occurred saving profile\nCode: 003";
            return response;
        }

        if (profile == null) {
            response.message = "Error occurred saving profile\nCode: 004";
        }

        List<Profile> profiles = Storage.getInstance().getProfiles();

        try {
            for (int i = 0; i < profiles.size(); ++i) {
                if (profiles.get(i).aNumber.equals(profile.aNumber)) {
                    profiles.set(i, profile);
                    Storage.getInstance().saveProfiles();
                    response.success = true;
                    response.message = "success";
                    return response;
                }
            }
        } catch (Exception e) {
            response.message = "Error occurred saving profile\nCode: 005";
            return response;
        }

        response.message = "Error occurred saving profile\nCode: 006";
        return response;
    }

    private static boolean nullOrWhitespace (String str) {
        return (str == null || str.trim().length() == 0);
    }
}
