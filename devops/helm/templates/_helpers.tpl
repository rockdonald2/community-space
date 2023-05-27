{{/* vim: set filetype=mustache: */}}

{{- define "primary.fullname" -}}
{{- printf .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "deployment" -}}
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Chart.Name }}-{{ .Release.Name }}-deployment
  labels:
    app.kubernetes.io/name: {{ .Chart.Name }}-{{ .Release.Name }}-deployment
spec:
  replicas: {{ .Values.replicas }}
  selector:
    matchLabels:
      app.kubernetes.io/name: {{ .Chart.Name }}-{{ .Release.Name }}-deployment
  template:
    metadata:
      labels:
        app.kubernetes.io/name: {{ .Chart.Name }}-{{ .Release.Name }}-deployment
      annotations:
        checksum/config: {{ include (print $.Template.BasePath "/configmap.yaml") . | sha256sum }}
    spec:
      containers:
        - name: {{ include "primary.fullname" . }}
          image: {{ .Values.image.name }}:{{ .Values.image.tag }}
          {{- if .Values.image.pullPolicy }}
          imagePullPolicy: {{ .Values.image.pullPolicy }}
          {{- else }}
          imagePullPolicy: IfNotPresent
          {{- end }}
          ports:
            {{- range .Values.service.ports }}
            - containerPort: {{ .port }}
              name: {{ .name }}
            {{- end }}
          {{- if .Values.env }}
          envFrom:
            - configMapRef:
                name: {{ .Chart.Name }}-{{ .Release.Name }}-configmap
          {{- end }}
          {{- if .Values.secrets }}
          env:
            {{- range .Values.secrets }}
            - name: {{ .envName }}
              valueFrom:
                secretKeyRef:
                  name: {{ .secretName }}
                  key: {{ .secretName }}
            {{- end }}
          {{- end }}
          {{- if .Values.resources }}
          resources:
            limits:
              cpu: {{ .Values.resources.limits.cpu }}
              memory: {{ .Values.resources.limits.memory }}
            {{- if .Values.resources.requests }}
            requests:
              {{- if .Values.resources.requests.cpu }}
              cpu: {{ .Values.resources.requests.cpu }}
              {{- end }}
              {{- if .Values.resources.requests.memory }}
              memory: {{ .Values.resources.requests.memory }}
              {{- end }}
            {{- end }}
          {{- end }}
          volumeMounts:
              {{- if .Values.nextjsEnv }}
              - mountPath: /app/.env.local
                name: {{ .Chart.Name }}-{{ .Release.Name }}-nextjs
                readOnly: true
                subPath: .env.local
              {{- end }}
          {{- if .Values.liveness }}
          livenessProbe:
            httpGet:
              path: {{ .Values.liveness.path }}
              port: {{ .Values.liveness.port }}
            initialDelaySeconds: 120
            periodSeconds: 10
            timeoutSeconds: 5
            failureThreshold: 5
          {{- end }}
          {{- if .Values.readiness }}
          readinessProbe:
            httpGet:
              path: {{ .Values.readiness.path }}
              port: {{ .Values.readiness.port }}
            initialDelaySeconds: 90
            periodSeconds: 10
            timeoutSeconds: 5
            failureThreshold: 5
          {{- end }}
      {{- if .Values.image.pullSecrets }}
      imagePullSecrets:
        - name: {{ .Values.image.pullSecrets }}
      {{- end }}
      volumes:
        {{- if .Values.nextjsEnv }}
        - name: {{ .Chart.Name }}-{{ .Release.Name }}-nextjs
          configMap:
            defaultMode: 420
            items:
            - key: .env
              path: .env.local
            name: {{ .Chart.Name }}-{{ .Release.Name }}-nextjs
        {{- end }}
{{- end -}}

{{- define "service" -}}
apiVersion: v1
kind: Service
metadata:
  name: {{ .Chart.Name }}-{{ .Release.Name }}-service
spec:
  type: {{ .Values.service.type }}
  selector:
    app.kubernetes.io/name: {{ .Chart.Name }}-{{ .Release.Name }}-deployment
  ports:
    {{- range .Values.service.ports }}
    - protocol: TCP
      port: {{ .port }}
      targetPort: {{ .port }}
      name: {{ .name }}
    {{- end }}
{{- end -}}

{{- define "ingress" -}}
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: {{ .Chart.Name }}-{{ .Release.Name }}-ingress
  annotations:
    nginx.ingress.kubernetes.io/use-regex: "true"
spec:
  ingressClassName: nginx
  rules:
     - http:
        paths:
          - path: {{ .Values.ingress.path }}
            pathType: Prefix
            backend:
              service:
                name: {{ .Chart.Name }}-{{ .Release.Name }}-service
                port:
                  number: {{ .Values.ingress.port }}
{{- end -}}

{{- define "hpa" }}
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: {{ .Chart.Name }}-{{ .Release.Name }}-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: {{ .Chart.Name }}-{{ .Release.Name }}-deployment
  minReplicas: {{ .Values.hpa.minReplicas }}
  maxReplicas: {{ .Values.hpa.maxReplicas }}
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          {{- if .Values.hpa.targetCpuUtilization }}
          averageUtilization: {{ .Values.hpa.targetCpuUtilization }}
          {{- else }}
          averageUtilization: 50
          {{- end }}
{{- end }}

{{- define "configmap" }}
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Chart.Name }}-{{ .Release.Name }}-configmap
data:
{{- toYaml .Values.env | nindent 2 }}
{{- end }}

{{- define "secrets" }}
{{- range .Values.secrets }}
---
apiVersion: v1
kind: Secret
metadata:
  name: {{ .secretName }}
type: Opaque
data:
  {{ .secretName }}: {{ .secretValue | b64enc | quote }}
{{- end }}
{{- end }}