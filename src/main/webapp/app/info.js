mcpInfo = {
  title: '${project.name}',
  version: '${project.version}',
  isInitialised: '${project.version}'.lastIndexOf('project.version') === -1,
  build: { // filled in by jenkins build job
    id: '${BUILD_ID}',
    tag: '${BUILD_TAG}',
    url: '${BUILD_URL}',
    number: '${BUILD_NUMBER}',
    isInitialised: '${BUILD_NUMBER}'.lastIndexOf('BUILD_NUMBER') === -1,
  }
};
