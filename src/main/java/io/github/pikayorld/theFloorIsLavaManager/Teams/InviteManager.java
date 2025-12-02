package io.github.pikayorld.theFloorIsLavaManager.Teams;

import java.util.*;


public class InviteManager {


    private final Map<UUID, String> invites = new HashMap<>();


    public void sendRequest(UUID player, String team) {
        invites.put(player, team);
    }

    public List<UUID> getListOfRequestToTeam(String name){
        List<UUID> listOfRequest = new ArrayList<>();

        for (Map.Entry<UUID,String> invite : invites.entrySet()){
            listOfRequest.add(invite.getKey());
        }
        return listOfRequest;
    }

    public void deleteAllInviteForTeam(String teamName){
        for (Map.Entry<UUID,String> invite : invites.entrySet()){
            if (Objects.equals(invite.getValue(), teamName)){
                remove(invite.getKey());
            }
        }
    }


    public String getInvite(UUID uuid) {
        return invites.get(uuid);
    }


    public void remove(UUID uuid) {
        invites.remove(uuid);
    }
}
