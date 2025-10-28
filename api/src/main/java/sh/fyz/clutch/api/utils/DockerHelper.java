package sh.fyz.clutch.api.utils;


public class DockerHelper {

    public static boolean isDockerEnv() {
        return System.getenv("DOCKER_RUNNING") != null;
    }

}
