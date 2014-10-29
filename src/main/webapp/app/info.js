mcpInfo = {
  title: '${project.name}',
  version: '${project.version}',
  isInitialised: '${project.version}'.lastIndexOf('project.version') === -1,
  build: { // filled in by jenkins build job
    id: '${env.BUILD_ID}',
    tag: '${env.BUILD_TAG}',
    url: '${env.BUILD_URL}',
    number: '${env.BUILD_NUMBER}',
    isInitialised: '${env.BUILD_NUMBER}'.lastIndexOf('env.BUILD_NUMBER') === -1
  }
};
