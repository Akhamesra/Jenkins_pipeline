pipeline{
    agent { node { label "$env.label"}}
    stages{
        stage('Code checkout'){
            steps{
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/Akhamesra/Jenkins_pipeline.git'
                echo 'Code Checkout Done'
            }
        }

        stage('Create a package'){
            steps{
                sh 'cd template'
                sh 'zip -r ../jenkins.zip *'
            }
        }

        stage('EC2 Instance'){
            steps{
                sshagent(['AWS_Key']) {
                    sh script:'''
                            
                            ssh -o StrictHostKeyChecking=no ec2-user@13.232.72.1 '
                                sudo su
                                sudo yum update -y
                                sudo yum install httpd -y
                                sudo service httpd start
                                sudo chkconfig httpd on
                                sudo cd /var/www/html
                                sudo chmod 777 /var/www/html
                            '
                            scp jenkins.zip ec2-user@13.232.72.1:/var/www/html

                            ssh ec2-user@13.232.72.1 '
                                cd /var/www/html
                                unzip jenkins.zip -d .
                            '
                            '''
                }
            }
        }
    }
}