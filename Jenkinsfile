
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        IMAGE_REPO = 'blk350101/finsecure-pay'
        AWS_DEFAULT_REGION = 'ap-southeast-1'
        EKS_CLUSTER = 'finsecure-eks-cluster'
        K8S_NAMESPACE = 'finsecure'
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh '''
                    chmod +x mvnw
                    ./mvnw -B clean verify
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        
stage('Dependency Security Scan') {
    steps {
        sh '''
            trivy fs \
              --scanners vuln \
              --severity HIGH,CRITICAL \
              --exit-code 1 .
        '''
    }
}

stage('Secret Detection') {
    steps {
        sh '''
            trivy fs \
              --scanners secret \
              --exit-code 1 .
        '''
    }
}


stage('Configuration Security Scan') {
    steps {
        sh '''
            trivy config \
              --config-data security \
              --ignorefile .trivyignore.yaml \
              --severity LOW,MEDIUM,HIGH,CRITICAL \
              --exit-code 1 \
              .
        '''
    }
}

}


        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                      -t "$IMAGE_REPO:$BUILD_NUMBER" .
                '''
            }
        }

        
stage('Container Security Scan') {
    steps {
        sh '''
            trivy image \
              --scanners vuln \
              --severity HIGH,CRITICAL \
              --exit-code 1 \
              "$IMAGE_REPO:$BUILD_NUMBER"
        '''
    }
}


        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x

                        echo "$DOCKER_TOKEN" | docker login \
                          --username "$DOCKER_USER" \
                          --password-stdin

                        docker push "$IMAGE_REPO:$BUILD_NUMBER"

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                    aws eks update-kubeconfig \
                      --region "$AWS_DEFAULT_REGION" \
                      --name "$EKS_CLUSTER" \
                      --kubeconfig "$KUBECONFIG"

                    kubectl set image \
                      deployment/finsecure-pay \
                      finsecure-pay="$IMAGE_REPO:$BUILD_NUMBER" \
                      -n "$K8S_NAMESPACE"

                    kubectl rollout status \
                      deployment/finsecure-pay \
                      -n "$K8S_NAMESPACE" \
                      --timeout=300s
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    kubectl get deployment \
                      finsecure-pay -n "$K8S_NAMESPACE"

                    kubectl get pods -n "$K8S_NAMESPACE"
                '''
            }
        }
    }

    post {
        success {
            echo 'FinSecure Pay CI/CD pipeline completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Check console output.'
        }
    }
}
