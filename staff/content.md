以下是为 MySQL 8 的三个 pod 创建 k8s 文件：

mysql-deployment.yaml:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql-cluster
spec:
  replicas: 3
  selector:
    matchLabels:
      app: mysql-pod
  strategy:
    type: Recreate
  template:
    metadata:
      labels:
        app: mysql-pod
    spec:
      containers:
      - image: mysql:8.0.29
        name: mysql-container
        env:
          - name: MYSQL_ROOT_PASSWORD
            valueFrom:
              secretKeyRef:
                name: mysql-secret
                key: password
        ports:
        - containerPort: 3306
```

mysql-service.yaml:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: mysql-cluster
spec:
  type: ClusterIP
  selector:
    app: mysql-pod
  ports:
  - port: 3306
    targetPort: 3306
```

mysql-pvc.yaml:
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-claim
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
```

mysql-secret.yaml:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
type: Opaque
data:
  password: <base64 encoded value of the password>
```

请确保在 base64 密码中使用正确的密码。你可以在这些文件的基础上创建集cluster。这些文件会在 Kubernetes中部署3个mysql pod, 一个service和存储。在生成base64密码之前，请确保在secret.yaml文件中的data.password中提供正确的密码。

你可以在k8s集群中使用如下命令创建这些文件：

```bash
kubectl apply -f mysql-deployment.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f mysql-pvc.yaml
kubectl apply -f mysql-secret.yaml
```