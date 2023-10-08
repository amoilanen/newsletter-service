import json
from kafka import KafkaProducer
from kafka.errors import KafkaError

producer = KafkaProducer(bootstrap_servers=['localhost:9092'])

newsletter_attributes = {
  'title': 'Amazing COBOL',
  'owner': {
    'name': 'Mikko Meikäläinen',
    'email': 'm.meikäläinen@elisa.fi'
  }
}

event = {
  'action': 'create',
  'newsletter_attributes': newsletter_attributes
}

future = producer.send('newsletters', json.dumps(event, indent = 2).encode('utf-8'))

try:
    record_metadata = future.get(timeout=10)
except KafkaError:
    log.exception()