
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '15'))
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
                    set -eu
                    chmod +x mvnw
                    ./mvnw -B clean verify
                '''
            }
            post {
                always {
                    junit(
                        allowEmptyResults: true,
                        testResults: 'target/surefire-reports/*.xml'
                    )
                }
            }
        }

        stage('Dependency Security Scan') {
            steps {
                sh '''
                    set -eu
                    mkdir -p security-reports

                    trivy fs \
                      --scanners vuln \
                      --severity HIGH,CRITICAL \
                      --ignorefile "" \
                      --format json \
                      --output security-reports/dependencies.json \
                      --exit-code 1 \
                      .
                '''
            }
        }

        stage('Secret Detection') {
            steps {
                sh '''
                    set -eu
                    trivy fs \
                      --scanners secret \
                      --ignorefile "" \
                      --exit-code 1 \
                      .
                '''
            }
        }

        stage('Configuration Security Scan') {
            steps {
                sh '''
                    set -eu
                    mkdir -p security-reports

                    trivy config \
                      --config-data security \
                      --ignorefile .trivyignore.yaml \
                      --severity LOW,MEDIUM,HIGH,CRITICAL \
                      --format json \
                      --output security-reports/configuration.json \
                      --exit-code 1 \
                      .
                '''
            }
        }
        
        stage('Security Gate Rejection Test') {
    steps {
        sh '''
            echo "CAPSTONE TEST: Simulated security policy rejection"
            exit 1
        '''
    }
}

        stage('Docker Build') {
            steps {
                sh '''
                    set -eu
                    docker build \
                      -t "$IMAGE_REPO:$BUILD_NUMBER" .
                '''
            }
        }

        stage('Container Security Scan') {
            steps {
                sh '''
                    set -eu
                    mkdir -p security-reports

                    trivy image \
                      --scanners vuln \
                      --severity HIGH,CRITICAL \
                      --format json \
                      --output security-reports/container.json \
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
                        set -eu
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
                    set -eu

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
                    set -eu

                    kubectl get deployment \
                      finsecure-pay \
                      -n "$K8S_NAMESPACE"

                    kubectl get pods \
                      -n "$K8S_NAMESPACE"

                    kubectl get deployment \
                      finsecure-pay \
                      -n "$K8S_NAMESPACE" \
                      -o jsonpath='{.spec.template.spec.containers[0].image}{"\\n"}'
                '''
            }
        }
    }

    post {
        always {
            archiveArtifacts(
                artifacts: 'security-reports/*.json',
                allowEmptyArchive: true
            )
        }

        success {
            echo 'SUCCESS: FinSecure Pay DevSecOps pipeline completed.'
        }

        failure {
            echo 'FAILURE: Check build, security scans, or deployment logs.'
        }
    }
}
