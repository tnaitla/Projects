#!/usr/bin/env python
# coding: utf-8

# #  Charegement et preparation  de la données 

# In[2]:


import pandas as pd
import sklearn.preprocessing as preprocessing
df = pd.read_csv("Data_for_prediction.csv",low_memory=False)
X = df
def encode(X,X_test,categorical_columns):
    le = preprocessing.LabelEncoder()
    XX = X
    for i in categorical_columns : 
        le = preprocessing.LabelEncoder()
        le.fit(XX[i].astype(str))
        print(list(le.classes_))
        X[i] = le.transform(X[i].astype(str))

encode(X,X,["genre"])

XX = pd.DataFrame()
for i in range(0,15) : 
    X_curr = X[X["genre"] == i][0:6000]
    XX = pd.concat([XX , X_curr] , ignore_index=True)
    
y = XX['genre']
XX = XX.drop(["genre"] , axis= 1 )
XX.head()
XX = XX.drop(["key"] , axis= 1 )
XX = XX.drop(["mode"] , axis= 1 )


# **Equilibrage des Données**

# In[3]:


from sklearn import preprocessing
import imblearn
from imblearn.over_sampling import SMOTE
smo = SMOTE()
X_sm, y_sm = smo.fit_resample(XX, y)

X_sm.shape


# In[4]:


X_sm.head()


# ***Sepration des data en ensemble de Test et de Train***

# In[5]:


from sklearn.model_selection import train_test_split
from sklearn.gaussian_process import GaussianProcessClassifier
from sklearn.gaussian_process.kernels import RBF
from sklearn.metrics import (
    accuracy_score,
    balanced_accuracy_score,
    roc_auc_score,
)
from sklearn.neural_network import MLPClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler


# In[16]:


X_train, X_test, y_train, y_test = train_test_split(X_sm, y_sm, test_size=0.33, random_state=42)
sc_X = StandardScaler()
X_trainscaled=sc_X.fit_transform(X_train)
X_testscaled=sc_X.transform(X_test)


# ***Entrainement du model et son score***

# In[17]:


clf = MLPClassifier(hidden_layer_sizes=(150,),activation="relu",random_state=1,max_iter= 1000,alpha= 0.001 ).fit(X_trainscaled, y_train)
y_pred = clf.predict(X_testscaled) # predictions
score = balanced_accuracy_score(y_test, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_test, y_pred) # scoring
print('accuracy score: {}'.format(score))


# In[21]:


import seaborn as sn
import matplotlib.pyplot as plt 
from sklearn.metrics import confusion_matrix
cm=confusion_matrix(y_test,y_pred)
df_cm= pd.DataFrame(cm)
plt.figure(figsize=(12,8))
sn.heatmap(df_cm, annot=True, annot_kws={"size": 8})


# In[ ]:





# In[8]:


mlp_gs = MLPClassifier(max_iter=1000)
parameter_space = {
    'hidden_layer_sizes': [(100,),(150,)],
    'alpha': [0.05 , 0.001 ],
    
}
from sklearn.model_selection import GridSearchCV
clf = GridSearchCV(mlp_gs, parameter_space, n_jobs=-1, cv=5)
clf.fit(X_trainscaled, y_train) # X is train samples and y is the corresponding labels


# In[9]:


print('Best parameters found:\n', clf.best_params_)


# In[10]:


means = clf.cv_results_['mean_test_score']
stds = clf.cv_results_['std_test_score']
for mean, std, params in zip(means, stds, clf.cv_results_['params']):
    print("%0.3f (+/-%0.03f) for %r" % (mean, std * 2, params))   


# In[11]:


from sklearn.metrics import plot_confusion_matrix
plot_confusion_matrix(clf, X_testscaled, y_test)


# In[26]:





# In[13]:


clf = MLPClassifier(hidden_layer_sizes=(150,),activation="relu",random_state=1,max_iter= 1000,alpha=0.05  ).fit(X_trainscaled, y_train)
from sklearn.metrics import (
    accuracy_score,
    balanced_accuracy_score,
    roc_auc_score,
)
y_pred = clf.predict(X_testscaled) # predictions
score = balanced_accuracy_score(y_test, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_test, y_pred) # scoring
print('accuracy score: {}'.format(score))

y_pred = clf.predict(X_trainscaled) # predictions
score = balanced_accuracy_score(y_train, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_train, y_pred) # scoring
print('accuracy score: {}'.format(score))


# In[20]:


clf = MLPClassifier(hidden_layer_sizes=(250,),activation="relu",random_state=42,max_iter= 1000,alpha=0.05  ).fit(X_trainscaled, y_train)
from sklearn.metrics import (
    accuracy_score,
    balanced_accuracy_score,
    roc_auc_score,
)
y_pred = clf.predict(X_testscaled) # predictions
score = balanced_accuracy_score(y_test, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_test, y_pred) # scoring
print('accuracy score: {}'.format(score))

y_pred = clf.predict(X_trainscaled) # predictions
score = balanced_accuracy_score(y_train, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_train, y_pred) # scoring
print('accuracy score: {}'.format(score))


# In[21]:


plot_confusion_matrix(clf, X_testscaled, y_test)


# In[15]:


#clf = MLPClassifier(hidden_layer_sizes=(150,),activation="relu",random_state=1,max_iter= 1000).fit(X_trainscaled, y_train)
from sklearn.metrics import (
    accuracy_score,
    balanced_accuracy_score,
    roc_auc_score,
)
y_pred = clf.predict(X_testscaled) # predictions
score = balanced_accuracy_score(y_test, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_test, y_pred) # scoring
print('accuracy score: {}'.format(score))


# In[16]:


y_pred = clf.predict(X_trainscaled) # predictions
score = balanced_accuracy_score(y_train, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_train, y_pred) # scoring
print('accuracy score: {}'.format(score))


# In[18]:


#clf = MLPClassifier(hidden_layer_sizes=(250,),activation="relu",random_state=42,max_iter= 800).fit(X_trainscaled, y_train)
from sklearn.metrics import (
    accuracy_score,
    balanced_accuracy_score,
    roc_auc_score,
)
y_pred = clf.predict(X_testscaled) # predictions
score = balanced_accuracy_score(y_test, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_test, y_pred) # scoring
print('accuracy score: {}'.format(score))

y_pred = clf.predict(X_trainscaled) # predictions
score = balanced_accuracy_score(y_train, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_train, y_pred) # scoring
print('accuracy score: {}'.format(score))


# In[1]:


plot_confusion_matrix(clf, X_testscaled, y_test)


# In[28]:


from sklearn.model_selection import learning_curve
import matplotlib.pyplot as plt

model = clf.best_estimator_
clf.best_score_


# In[33]:


N, train_score, val_score = learning_curve(model,X_trainscaled, y_train, train_sizes = np.linspace(0.2, 1.0, 5), cv = 3)


# In[34]:


print(N) 
plt.plot(N,train_score.mean(axis=1),label='train')
plt.plot(N,val_score.mean(axis=1),label='val')
plt.xlabel('train_sizes')
plt.legend()


# In[37]:


# Create training and test split
#
X_train, X_test, y_train, y_test = train_test_split(X_sm, y_sm, test_size=0.3, random_state=42)
# Create a pipeline; This will be passed as an estimator to learning curve method
#
#pipeline = make_pipeline(StandardScaler(),
 #                       LogisticRegression(penalty='l2', solver='lbfgs', random_state=1, max_iter=10000))
#
# Use learning curve to get training and test scores along with train sizes
#
train_sizes, train_scores, test_scores = learning_curve(model,X_trainscaled, y_train, train_sizes = np.linspace(0.2, 1.0, 5), cv = 3)
# Calculate training and test mean and std
#
train_mean = np.mean(train_scores, axis=1)
train_std = np.std(train_scores, axis=1)
test_mean = np.mean(test_scores, axis=1)
test_std = np.std(test_scores, axis=1)
#
# Plot the learning curve
#
plt.plot(train_sizes, train_mean, color='blue', marker='o', markersize=5, label='Training Accuracy')
plt.fill_between(train_sizes, train_mean + train_std, train_mean - train_std, alpha=0.15, color='blue')
plt.plot(train_sizes, test_mean, color='green', marker='+', markersize=5, linestyle='--', label='Validation Accuracy')
plt.fill_between(train_sizes, test_mean + test_std, test_mean - test_std, alpha=0.15, color='green')
plt.title('Learning Curve')
plt.xlabel('Training Data Size')
plt.ylabel('Model accuracy')
plt.grid()
plt.legend(loc='lower right')
plt.show()


# In[ ]:



clf = MLPClassifier(hidden_layer_sizes=(250,),activation="relu",random_state=42,max_iter=1500 , learning_rate_init = 0.0001).fit(X_trainscaled, y_train)
from sklearn.metrics import (
    accuracy_score,
    balanced_accuracy_score,
    roc_auc_score,
)
y_pred = clf.predict(X_testscaled) # predictions
score = balanced_accuracy_score(y_test, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_test, y_pred) # scoring
print('accuracy score: {}'.format(score))

y_pred = clf.predict(X_trainscaled) # predictions
score = balanced_accuracy_score(y_train, y_pred) # scoring
print('balanced_accuracy score: {}'.format(score))
score = accuracy_score(y_train, y_pred) # scoring
print('accuracy score: {}'.format(score))


# In[23]:


import numpy as np 
from sklearn import svm, datasets
from sklearn.model_selection import GridSearchCV
from sklearn.pipeline import Pipeline

standardScalar = StandardScaler()
mlp = MLPClassifier()

pipe = Pipeline(steps=[('standardScalar', standardScalar), ('mlp', mlp)])


#param_grid = { 'svm__C':[1,100],'svm__kernel':('sigmoid','rbf')  }
parameter_space = {
    'mlp__hidden_layer_sizes':np.arange(50,250,50),
    'mlp__activation': [ 'relu', 'tanh'],
    'mlp__solver': [ 'adam' ],
    'mlp__alpha': [ 0.001 ],
    'mlp__learning_rate_init' :np.linspace(0.0001 ,0.01,6),
    'mlp__learning_rate': ['constant'],
    'mlp__max_iter':[1400],
    'mlp__verbose':['True']
}
from sklearn.model_selection import GridSearchCV
grid = GridSearchCV(pipe, parameter_space, cv=3 , return_train_score = True )
grid.fit(X_sm, y_sm) # X is train samples and y is the corresponding labels


# In[24]:


grid.best_score_


# In[21]:


print('Best parameters found:\n', clf.best_params_)


# In[25]:


grid.cv_results_



# In[26]:


df= pd.DataFrame(grid.cv_results_)


# In[27]:


df.head()


# In[28]:


df1= df.loc[df['param_mlp__activation'] == 'relu'] 
df3= df.loc[df['param_mlp__activation'] == 'tanh']
df3.head()


# In[29]:


df2=df1[0:10:2]
df2.plot('param_mlp__hidden_layer_sizes',['mean_test_score','mean_train_score'],kind = 'line')


# In[30]:


df2.head()


# In[31]:


df2.plot('param_mlp__hidden_layer_sizes',['mean_test_score','mean_train_score'],kind = 'line')


# In[35]:


df4=df3[0:100:5]
df4.plot('param_mlp__hidden_layer_sizes',['mean_test_score','mean_train_score'],kind = 'line')


# In[39]:


import matplotlib.pyplot as plt
plt.figure(figsize=(10,10))
plt.plot(df1['param_mlp__hidden_layer_sizes'],df1['mean_test_score'],label='relu_test')
plt.plot(df1['param_mlp__hidden_layer_sizes'],df1['mean_train_score'],label='relu_train')
plt.plot(df3['param_mlp__hidden_layer_sizes'],df3['mean_train_score'],label='tanh_train')
plt.plot(df3['param_mlp__hidden_layer_sizes'],df3['mean_test_score'],label='tanh_test')
plt.legend()


# In[38]:


df1=df[0:70:2]
df1.plot('params',['mean_test_score','mean_train_score'],kind = 'line')


# In[ ]:




